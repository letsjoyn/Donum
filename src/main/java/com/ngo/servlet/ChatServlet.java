package com.ngo.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ngo.dao.DashboardDAO;
import com.ngo.dao.CampaignDAO;
import com.ngo.dao.RequirementDAO;
import com.ngo.model.Campaign;
import com.ngo.model.Requirement;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@WebServlet("/api/chat")
public class ChatServlet extends HttpServlet {

    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY") != null
            ? System.getenv("GEMINI_API_KEY") : "";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read user message
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject incoming = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String userMessage = incoming.get("message").getAsString();

        // Sanitize - limit length
        if (userMessage.length() > 500) {
            userMessage = userMessage.substring(0, 500);
        }

        // Get live context from database
        String systemContext = buildSystemContext(request);

        String reply;
        if (GEMINI_API_KEY.isEmpty()) {
            // Fallback: rule-based responses when no API key
            reply = getFallbackResponse(userMessage, request);
        } else {
            reply = callGemini(userMessage, systemContext);
        }

        JsonObject result = new JsonObject();
        result.addProperty("reply", reply);
        response.getWriter().write(gson.toJson(result));
    }

    private String buildSystemContext(HttpServletRequest request) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("You are Donum AI, the assistant for Donum NGO Relief & Distribution Platform. ");
        ctx.append("Be concise (2-4 sentences max). Be helpful about donations, campaigns, and relief work. ");
        ctx.append("If asked anything unrelated to NGO/donations/disaster relief, politely redirect. ");

        try {
            // Pull live stats
            DashboardDAO dashDao = new DashboardDAO();
            Map<String, Object> stats = dashDao.getAdminStats();
            ctx.append("\nLive Platform Stats: ");
            ctx.append("Total Donations: ").append(stats.get("totalDonations")).append(", ");
            ctx.append("Cash Raised: Rs ").append(stats.get("totalCashRaised")).append(", ");
            ctx.append("Active Campaigns: ").append(stats.get("activeCampaigns")).append(", ");
            ctx.append("Items Distributed: ").append(stats.get("itemsDistributed")).append(". ");

            // Active campaigns
            CampaignDAO cDao = new CampaignDAO();
            List<Campaign> campaigns = cDao.getActiveCampaigns();
            ctx.append("\nActive Campaigns: ");
            for (Campaign c : campaigns) {
                ctx.append(c.getName()).append(" (raised Rs ").append(c.getRaisedAmount())
                   .append(" of Rs ").append(c.getTargetAmount()).append("), ");
            }

            // Urgent requirements
            RequirementDAO rDao = new RequirementDAO();
            List<Requirement> reqs = rDao.getPendingRequirements();
            int critCount = 0;
            ctx.append("\nUrgent Needs: ");
            for (Requirement r : reqs) {
                if ("Critical".equals(r.getUrgency()) && critCount < 3) {
                    ctx.append(r.getItemName()).append(" at ").append(r.getLocation()).append(", ");
                    critCount++;
                }
            }
        } catch (Exception e) {
            ctx.append("\n(Could not load live data) ");
        }

        // User context
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            ctx.append("\nCurrent user: ").append(user.getFullName())
               .append(" (").append(user.getRole()).append("). ");
        }

        return ctx.toString();
    }

    private String callGemini(String userMessage, String systemContext) {
        try {
            URL url = new URL(GEMINI_URL + GEMINI_API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            // Build Gemini request
            JsonObject requestBody = new JsonObject();

            // System instruction
            JsonObject systemInstruction = new JsonObject();
            JsonObject systemPart = new JsonObject();
            systemPart.addProperty("text", systemContext);
            JsonArray systemParts = new JsonArray();
            systemParts.add(systemPart);
            systemInstruction.add("parts", systemParts);
            requestBody.add("system_instruction", systemInstruction);

            // User message
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            content.addProperty("role", "user");
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            part.addProperty("text", userMessage);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);

            // Generation config
            JsonObject genConfig = new JsonObject();
            genConfig.addProperty("maxOutputTokens", 200);
            genConfig.addProperty("temperature", 0.7);
            requestBody.add("generationConfig", genConfig);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String l;
                    while ((l = br.readLine()) != null) resp.append(l);

                    JsonObject jsonResp = JsonParser.parseString(resp.toString()).getAsJsonObject();
                    return jsonResp
                            .getAsJsonArray("candidates").get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts").get(0).getAsJsonObject()
                            .get("text").getAsString().trim();
                }
            } else {
                return getFallbackResponse(userMessage, null);
            }
        } catch (Exception e) {
            return getFallbackResponse(userMessage, null);
        }
    }

    private String getFallbackResponse(String message, HttpServletRequest request) {
        String msg = message.toLowerCase();

        if (msg.contains("donate") || msg.contains("donation") || msg.contains("give") || msg.contains("contribute")) {
            return "You can donate by going to your Donor Dashboard and selecting a campaign. "
                 + "We accept both cash donations (via UPI, bank transfer) and in-kind donations "
                 + "(food, blankets, medical supplies). Every contribution counts!";
        }

        if (msg.contains("campaign") || msg.contains("relief") || msg.contains("disaster")) {
            try {
                CampaignDAO cDao = new CampaignDAO();
                List<Campaign> campaigns = cDao.getActiveCampaigns();
                StringBuilder sb = new StringBuilder("We currently have " + campaigns.size() + " active campaigns: ");
                for (int i = 0; i < Math.min(campaigns.size(), 3); i++) {
                    Campaign c = campaigns.get(i);
                    sb.append(c.getName());
                    int pct = c.getTargetAmount() > 0
                            ? (int)((c.getRaisedAmount() / c.getTargetAmount()) * 100) : 0;
                    sb.append(" (").append(pct).append("% funded)");
                    if (i < Math.min(campaigns.size(), 3) - 1) sb.append(", ");
                }
                sb.append(". Visit the Campaigns page to see all details.");
                return sb.toString();
            } catch (Exception e) {
                return "We have several active campaigns for disaster relief. Check the Campaigns page for details!";
            }
        }

        if (msg.contains("urgent") || msg.contains("need") || msg.contains("require") || msg.contains("critical")) {
            try {
                RequirementDAO rDao = new RequirementDAO();
                List<Requirement> reqs = rDao.getPendingRequirements();
                long critical = reqs.stream().filter(r -> "Critical".equals(r.getUrgency())).count();
                return "There are currently " + critical + " critical requirements pending. "
                     + "The most urgent needs include food, medical kits, and water purification supplies "
                     + "for disaster-affected areas. Donors can help by contributing to active campaigns.";
            } catch (Exception e) {
                return "We have several urgent requirements. Check with the admin dashboard for details.";
            }
        }

        if (msg.contains("volunteer") || msg.contains("help") || msg.contains("join")) {
            return "To volunteer, register on the platform with the 'Volunteer' role. "
                 + "Volunteers help distribute relief supplies to disaster-affected areas. "
                 + "You'll be able to see pending requirements and log your distributions.";
        }

        if (msg.contains("receipt") || msg.contains("tax") || msg.contains("80g")) {
            return "After donating, you can download a PDF receipt from your Donor Dashboard. "
                 + "Cash donations to registered NGOs are eligible for tax deductions under "
                 + "Section 80G of the Income Tax Act.";
        }

        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey")) {
            return "Hello! I'm Donum AI, your relief platform assistant. I can help you with "
                 + "donations, campaigns, volunteering, and understanding our impact. What would you like to know?";
        }

        if (msg.contains("how") && msg.contains("work")) {
            return "Donum connects donors, NGOs, and volunteers: Donors contribute cash or goods to campaigns. "
                 + "Admins manage inventory and post requirements. Volunteers deliver supplies to affected areas. "
                 + "Our AI matching algorithm optimizes distribution based on urgency and availability.";
        }

        return "I can help with: donations, active campaigns, urgent requirements, volunteering, "
             + "tax receipts, or how Donum works. What would you like to know?";
    }
}

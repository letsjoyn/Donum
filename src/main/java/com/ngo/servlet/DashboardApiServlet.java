package com.ngo.servlet;

import com.ngo.dao.DashboardDAO;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import com.ngo.model.User;

@WebServlet("/api/dashboard/*")
public class DashboardApiServlet extends HttpServlet {
    private DashboardDAO dashboardDAO = new DashboardDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !"Admin".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Admin login required\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "/stats";

        Object data;
        switch (pathInfo) {
            case "/stats":
                data = dashboardDAO.getAdminStats();
                break;
            case "/trends":
                data = dashboardDAO.getMonthlyDonationTrends();
                break;
            case "/campaigns":
                data = dashboardDAO.getCampaignProgress();
                break;
            case "/donation-types":
                data = dashboardDAO.getDonationsByType();
                break;
            case "/urgency":
                data = dashboardDAO.getUrgencyDistribution();
                break;
            default:
                response.setStatus(404);
                data = Collections.singletonMap("error", "Endpoint not found");
        }

        response.getWriter().write(gson.toJson(data));
    }
}

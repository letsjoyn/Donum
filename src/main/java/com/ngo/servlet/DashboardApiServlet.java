package com.ngo.servlet;

import com.ngo.dao.DashboardDAO;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/dashboard/*")
public class DashboardApiServlet extends HttpServlet {
    private DashboardDAO dashboardDAO = new DashboardDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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

package com.ngo.servlet;

import com.ngo.dao.RequirementDAO;
import com.ngo.model.Requirement;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/requirements")
public class RequirementServlet extends HttpServlet {
    private RequirementDAO requirementDAO = new RequirementDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"Admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String location = request.getParameter("location");
        String itemName = request.getParameter("itemName");
        String qtyStr = request.getParameter("quantityNeeded");
        String urgency = request.getParameter("urgency");
        String campaignIdStr = request.getParameter("campaignId");
        String description = request.getParameter("description");

        if (location == null || itemName == null || qtyStr == null ||
            location.trim().isEmpty() || itemName.trim().isEmpty()) {
            response.sendRedirect("admin-dashboard.jsp?error=missing_fields");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr.trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            response.sendRedirect("admin-dashboard.jsp?error=invalid_quantity");
            return;
        }

        Requirement r = new Requirement();
        r.setLocation(location.trim());
        r.setItemName(itemName.trim());
        r.setQuantityNeeded(quantity);
        r.setUrgency(urgency != null ? urgency : "Medium");
        r.setDescription(description);

        if (campaignIdStr != null && !campaignIdStr.trim().isEmpty()) {
            try {
                r.setCampaignId(Integer.parseInt(campaignIdStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (requirementDAO.addRequirement(r)) {
            response.sendRedirect("admin-dashboard.jsp?success=req_added");
        } else {
            response.sendRedirect("admin-dashboard.jsp?error=req_failed");
        }
    }
}

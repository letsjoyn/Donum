package com.ngo.servlet;

import com.ngo.dao.DistributionDAO;
import com.ngo.model.DistributionLog;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/distribute")
public class DistributeServlet extends HttpServlet {
    private DistributionDAO distributionDAO = new DistributionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"Volunteer".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String reqIdStr = request.getParameter("requirementId");
            String qtyStr = request.getParameter("quantity");
            String notes = request.getParameter("notes");

            if (reqIdStr == null || qtyStr == null || reqIdStr.trim().isEmpty() || qtyStr.trim().isEmpty()) {
                response.sendRedirect("volunteer-dashboard.jsp?error=missing_fields");
                return;
            }

            int requirementId = Integer.parseInt(reqIdStr.trim());
            double quantity = Double.parseDouble(qtyStr.trim());

            if (quantity <= 0 || quantity > 100000) {
                response.sendRedirect("volunteer-dashboard.jsp?error=invalid_quantity");
                return;
            }

            DistributionLog log = new DistributionLog();
            log.setRequirementId(requirementId);
            log.setQuantityDistributed(quantity);
            log.setVolunteerId(user.getUserId());
            log.setNotes(notes);

            if (distributionDAO.logDistribution(log)) {
                response.sendRedirect("volunteer-dashboard.jsp?success=1");
            } else {
                response.sendRedirect("volunteer-dashboard.jsp?error=failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("volunteer-dashboard.jsp?error=invalid_input");
        }
    }
}

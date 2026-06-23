package com.ngo.servlet;

import com.ngo.dao.DistributionDAO;
import com.ngo.dao.DonationDAO;
import com.ngo.dao.RequirementDAO;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/delete")
public class AdminDeleteServlet extends HttpServlet {

    private final DonationDAO donationDAO = new DonationDAO();
    private final RequirementDAO requirementDAO = new RequirementDAO();
    private final DistributionDAO distributionDAO = new DistributionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"Admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String entity = request.getParameter("entity");
        String idParam = request.getParameter("id");
        if (entity == null || idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?error=invalid_delete");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?error=invalid_delete");
            return;
        }

        boolean ok = false;
        switch (entity) {
            case "donation":
                ok = donationDAO.deleteDonation(id);
                break;
            case "requirement":
                ok = requirementDAO.deleteRequirement(id);
                break;
            case "distribution":
                ok = distributionDAO.deleteDistribution(id);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?error=invalid_delete");
                return;
        }

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?deleted=" + entity);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?error=delete_failed");
        }
    }
}

package com.ngo.servlet;

import com.ngo.dao.DonationDAO;
import com.ngo.model.Donation;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/donate")
public class DonationServlet extends HttpServlet {
    private DonationDAO donationDAO = new DonationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String type = request.getParameter("type");
        String itemName = request.getParameter("itemName");
        String amountStr = request.getParameter("amount");
        String campaignIdStr = request.getParameter("campaignId");
        String notes = request.getParameter("notes");

        // Input validation
        if (type == null || amountStr == null || amountStr.trim().isEmpty()) {
            response.sendRedirect("donor-dashboard.jsp?error=missing_fields");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0 || amount > 100000000) {
                response.sendRedirect("donor-dashboard.jsp?error=invalid_amount");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("donor-dashboard.jsp?error=invalid_amount");
            return;
        }

        if ("Kind".equals(type) && (itemName == null || itemName.trim().isEmpty())) {
            response.sendRedirect("donor-dashboard.jsp?error=item_required");
            return;
        }

        Donation donation = new Donation();
        donation.setDonorId(user.getUserId());
        donation.setType(type);
        donation.setItemName("Kind".equals(type) ? itemName.trim() : null);
        donation.setAmountOrQuantity(amount);
        donation.setStatus("Received");
        donation.setNotes(notes);

        if (campaignIdStr != null && !campaignIdStr.trim().isEmpty()) {
            try {
                donation.setCampaignId(Integer.parseInt(campaignIdStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (donationDAO.addDonation(donation)) {
            response.sendRedirect("donor-dashboard.jsp?success=1");
        } else {
            response.sendRedirect("donor-dashboard.jsp?error=failed");
        }
    }
}

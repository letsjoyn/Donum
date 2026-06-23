package com.ngo.servlet;

import com.ngo.dao.UserDAO;
import com.ngo.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User sessionUser = getSessionUser(request);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User fresh = userDAO.getUserById(sessionUser.getUserId());
        if (fresh != null) {
            request.setAttribute("profileUser", fresh);
        } else {
            request.setAttribute("profileUser", sessionUser);
        }
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User sessionUser = getSessionUser(request);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (fullName == null || fullName.isEmpty() || email == null || email.isEmpty()) {
            forwardWithError(request, response, sessionUser, "Full name and email are required.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            forwardWithError(request, response, sessionUser, "Invalid email format.");
            return;
        }

        if (userDAO.emailExistsForOtherUser(email, sessionUser.getUserId())) {
            forwardWithError(request, response, sessionUser, "This email is already used by another account.");
            return;
        }

        User updated = userDAO.getUserById(sessionUser.getUserId());
        if (updated == null) {
            forwardWithError(request, response, sessionUser, "User not found.");
            return;
        }

        updated.setFullName(fullName);
        updated.setEmail(email);
        updated.setPhone(phone != null && !phone.isEmpty() ? phone : null);
        updated.setAddress(address != null && !address.isEmpty() ? address : null);

        if (!userDAO.updateProfile(updated)) {
            forwardWithError(request, response, sessionUser, "Could not update profile. Please try again.");
            return;
        }

        boolean passwordChanged = false;
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) {
                forwardWithError(request, response, sessionUser, "New password must be at least 6 characters.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                forwardWithError(request, response, sessionUser, "New passwords do not match.");
                return;
            }
            if (!userDAO.changePassword(sessionUser.getUserId(), newPassword)) {
                forwardWithError(request, response, sessionUser, "Profile saved but password change failed.");
                return;
            }
            passwordChanged = true;
        }

        User fresh = userDAO.getUserById(sessionUser.getUserId());
        if (fresh != null) {
            HttpSession session = request.getSession(false);
            session.setAttribute("user", fresh);
        }

        String msg = passwordChanged ? "profile_updated_password" : "profile_updated";
        response.sendRedirect(request.getContextPath() + "/profile?success=" + msg);
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  User sessionUser, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        User draft = userDAO.getUserById(sessionUser.getUserId());
        if (draft == null) draft = sessionUser;

        draft.setFullName(trim(request.getParameter("fullName")));
        draft.setEmail(trim(request.getParameter("email")));
        draft.setPhone(trim(request.getParameter("phone")));
        draft.setAddress(trim(request.getParameter("address")));

        request.setAttribute("profileUser", draft);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    private User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("user");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

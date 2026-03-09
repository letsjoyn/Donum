package com.ngo.servlet;

import com.ngo.dao.UserDAO;
import com.ngo.model.User;
import com.ngo.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            response.sendRedirect(getDashboardUrl(user.getRole()));
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        User loggedInUser = userDAO.login(username.trim(), password);

        if (loggedInUser != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", loggedInUser);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            logAudit(loggedInUser.getUserId(), "LOGIN", "USER", loggedInUser.getUserId(),
                     "Login successful", request.getRemoteAddr());

            response.sendRedirect(getDashboardUrl(loggedInUser.getRole()));
        } else {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private String getDashboardUrl(String role) {
        switch (role) {
            case "Admin":     return "admin-dashboard.jsp";
            case "Volunteer": return "volunteer-dashboard.jsp";
            default:          return "donor-dashboard.jsp";
        }
    }

    private void logAudit(int userId, String action, String entityType, int entityId, String details, String ip) {
        String sql = "INSERT INTO audit_log (user_id, action, entity_type, entity_id, details, ip_address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, entityType);
            ps.setInt(4, entityId);
            ps.setString(5, details);
            ps.setString(6, ip);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

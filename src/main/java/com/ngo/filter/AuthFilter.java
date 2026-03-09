package com.ngo.filter;

import com.ngo.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/login", "/login.jsp", "/register", "/register.jsp", "/logout",
        "/css/", "/js/", "/images/", "/favicon.ico"
    );

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow public resources
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath) || path.equals("/")) {
                chain.doFilter(req, res);
                return;
            }
        }

        // Check authentication
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Role-based access control
        String role = user.getRole();
        if (path.startsWith("/admin") && !"Admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/" + role.toLowerCase() + "-dashboard.jsp");
            return;
        }

        // Security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
    <c:set var="pageTitle" value="Login" />
    <%@ include file="/WEB-INF/header.jspf" %>

        <div class="auth-container">
            <div class="glass-card">
                <h2><i class="fas fa-hand-holding-heart" style="color:var(--primary);"></i> Donum</h2>
                <p class="subtitle">Sign in to your account</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${fn:escapeXml(error)}
                    </div>
                </c:if>
                <c:if test="${param.registered == '1'}">
                    <div class="alert alert-success"><i class="fas fa-check-circle"></i> Account created! Please sign
                        in.</div>
                </c:if>
                <c:if test="${param.logout == '1'}">
                    <div class="alert alert-info"><i class="fas fa-info-circle"></i> You have been logged out.</div>
                </c:if>

                <form action="login" method="post">
                    <div class="form-group">
                        <label for="username"><i class="fas fa-user"></i> Username</label>
                        <input type="text" id="username" name="username" required placeholder="Enter your username"
                            autocomplete="username" maxlength="50">
                    </div>
                    <div class="form-group">
                        <label for="password"><i class="fas fa-lock"></i> Password</label>
                        <input type="password" id="password" name="password" required placeholder="Enter your password"
                            autocomplete="current-password" maxlength="100">
                    </div>
                    <button type="submit" class="btn-primary" style="width:100%;">
                        <i class="fas fa-sign-in-alt"></i> Sign In
                    </button>
                </form>

                <div class="auth-footer">
                    Don't have an account? <a href="register">Create one</a>
                </div>

                <div class="demo-creds">
                    <strong><i class="fas fa-key"></i> Demo Credentials:</strong><br>
                    Admin: <code>admin</code> / <code>admin123</code><br>
                    Donor: <code>donor1</code> / <code>donor123</code><br>
                    Volunteer: <code>vol1</code> / <code>vol123</code>
                </div>
            </div>
        </div>

        <%@ include file="/WEB-INF/footer.jspf" %>
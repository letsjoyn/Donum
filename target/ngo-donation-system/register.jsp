<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
    <c:set var="pageTitle" value="Register" />
    <%@ include file="/WEB-INF/header.jspf" %>

        <div class="auth-container">
            <div class="glass-card">
                <h2><i class="fas fa-user-plus" style="color:var(--primary);"></i> Create Account</h2>
                <p class="subtitle">Join the Donum Community</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${fn:escapeXml(error)}
                    </div>
                </c:if>

                <form action="register" method="post">
                    <div class="form-group">
                        <label for="fullName"><i class="fas fa-id-card"></i> Full Name</label>
                        <input type="text" id="fullName" name="fullName" required placeholder="Your full name"
                            maxlength="100" value="${fn:escapeXml(param.fullName)}">
                    </div>
                    <div class="form-group">
                        <label for="username"><i class="fas fa-user"></i> Username</label>
                        <input type="text" id="username" name="username" required placeholder="Choose a username"
                            maxlength="50" value="${fn:escapeXml(param.username)}">
                    </div>
                    <div class="form-group">
                        <label for="email"><i class="fas fa-envelope"></i> Email</label>
                        <input type="email" id="email" name="email" required placeholder="your@email.com"
                            maxlength="100" value="${fn:escapeXml(param.email)}">
                    </div>
                    <div class="form-group">
                        <label for="phone"><i class="fas fa-phone"></i> Phone (optional)</label>
                        <input type="tel" id="phone" name="phone" placeholder="+91 XXXXX XXXXX" maxlength="20"
                            value="${fn:escapeXml(param.phone)}">
                    </div>
                    <div class="form-group">
                        <label for="role"><i class="fas fa-user-tag"></i> I want to</label>
                        <select id="role" name="role" required>
                            <option value="Donor" ${param.role=='Donor' ? 'selected' : '' }>Donate (Donor)</option>
                            <option value="Volunteer" ${param.role=='Volunteer' ? 'selected' : '' }>Volunteer</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="password"><i class="fas fa-lock"></i> Password</label>
                        <input type="password" id="password" name="password" required placeholder="Minimum 6 characters"
                            minlength="6" maxlength="100">
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword"><i class="fas fa-lock"></i> Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required
                            placeholder="Re-enter password" maxlength="100">
                    </div>
                    <button type="submit" class="btn-primary" style="width:100%;">
                        <i class="fas fa-user-plus"></i> Create Account
                    </button>
                </form>

                <div class="auth-footer">
                    Already have an account? <a href="login">Sign in</a>
                </div>
            </div>
        </div>

        <%@ include file="/WEB-INF/footer.jspf" %>
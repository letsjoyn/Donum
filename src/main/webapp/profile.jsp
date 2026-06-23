<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="My Profile" />
<%@ include file="/WEB-INF/header.jspf" %>

<%
    com.ngo.model.User user = (com.ngo.model.User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    com.ngo.model.User profile = (com.ngo.model.User) request.getAttribute("profileUser");
    if (profile == null) profile = user;
    request.setAttribute("profile", profile);
    String dashUrl = "donor-dashboard.jsp";
    if ("Admin".equals(user.getRole())) dashUrl = "admin-dashboard.jsp";
    else if ("Volunteer".equals(user.getRole())) dashUrl = "volunteer-dashboard.jsp";
    request.setAttribute("dashUrl", dashUrl);
%>

<div class="page-header">
    <h1><i class="fas fa-user-edit"></i> Edit Profile</h1>
    <p>Changes are saved to the <strong>users</strong> table in MySQL.</p>
</div>

<c:if test="${param.success == 'profile_updated'}">
    <div class="alert alert-success"><i class="fas fa-check-circle"></i> Profile updated successfully.</div>
</c:if>
<c:if test="${param.success == 'profile_updated_password'}">
    <div class="alert alert-success"><i class="fas fa-check-circle"></i> Profile and password updated successfully.</div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${fn:escapeXml(error)}</div>
</c:if>

<div class="content-grid" style="max-width:720px;">
    <div class="glass-card">
        <h3 class="section-title"><i class="fas fa-id-card"></i> Account details</h3>

        <div style="margin-bottom:1.25rem;padding:0.75rem 1rem;background:rgba(255,255,255,0.04);border-radius:10px;">
            <div><strong>Username:</strong> ${fn:escapeXml(profile.username)} <span style="color:#94a3b8;">(cannot change)</span></div>
            <div style="margin-top:0.35rem;"><strong>Role:</strong>
                <span class="role-badge role-${fn:toLowerCase(profile.role)}">${profile.role}</span>
            </div>
            <c:if test="${not empty profile.createdAt}">
                <div style="margin-top:0.35rem;"><strong>Member since:</strong>
                    <fmt:formatDate value="${profile.createdAt}" pattern="dd MMM yyyy" />
                </div>
            </c:if>
        </div>

        <form action="profile" method="post">
            <div class="form-group">
                <label for="fullName"><i class="fas fa-user"></i> Full Name</label>
                <input type="text" id="fullName" name="fullName" required maxlength="100"
                    value="${fn:escapeXml(profile.fullName)}">
            </div>
            <div class="form-group">
                <label for="email"><i class="fas fa-envelope"></i> Email</label>
                <input type="email" id="email" name="email" required maxlength="100"
                    value="${fn:escapeXml(profile.email)}">
            </div>
            <div class="form-group">
                <label for="phone"><i class="fas fa-phone"></i> Phone</label>
                <input type="tel" id="phone" name="phone" maxlength="20"
                    value="${fn:escapeXml(profile.phone)}">
            </div>
            <div class="form-group">
                <label for="address"><i class="fas fa-map-marker-alt"></i> Address</label>
                <textarea id="address" name="address" rows="2" maxlength="500"
                    placeholder="City, state (optional)">${fn:escapeXml(profile.address)}</textarea>
            </div>

            <hr style="border-color:rgba(255,255,255,0.08);margin:1.5rem 0;">

            <h3 class="section-title" style="font-size:1rem;"><i class="fas fa-lock"></i> Change password (optional)</h3>
            <p style="color:#94a3b8;font-size:0.85rem;margin-bottom:1rem;">Leave blank to keep current password.</p>

            <div class="form-group">
                <label for="newPassword">New password</label>
                <input type="password" id="newPassword" name="newPassword" minlength="6" maxlength="100"
                    placeholder="Minimum 6 characters" autocomplete="new-password">
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirm new password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" maxlength="100"
                    placeholder="Re-enter new password" autocomplete="new-password">
            </div>

            <div style="display:flex;gap:0.75rem;flex-wrap:wrap;margin-top:1rem;">
                <button type="submit" class="btn-primary">
                    <i class="fas fa-save"></i> Save to database
                </button>
                <a href="${dashUrl}" class="btn-primary"
                    style="text-decoration:none;background:rgba(255,255,255,0.08);">
                    <i class="fas fa-arrow-left"></i> Back to dashboard
                </a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>

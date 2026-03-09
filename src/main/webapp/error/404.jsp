<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found | Donum</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <div class="auth-container" style="margin-top:8rem;">
        <div class="glass-card" style="text-align:center;">
            <i class="fas fa-map-signs" style="font-size:4rem;color:var(--primary);margin-bottom:1.5rem;opacity:0.6;"></i>
            <h2 style="-webkit-text-fill-color:var(--text-main);">404 - Page Not Found</h2>
            <p class="subtitle">The page you are looking for doesn't exist or has been moved.</p>
            <a href="${pageContext.request.contextPath}/login" class="btn-primary" style="margin-top:1rem;">
                <i class="fas fa-home"></i> Go Home
            </a>
        </div>
    </div>
</body>
</html>

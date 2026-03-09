<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Server Error | Donum</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <div class="auth-container" style="margin-top:8rem;">
        <div class="glass-card" style="text-align:center;">
            <i class="fas fa-exclamation-triangle" style="font-size:4rem;color:var(--danger);margin-bottom:1.5rem;opacity:0.6;"></i>
            <h2 style="-webkit-text-fill-color:var(--text-main);">500 - Server Error</h2>
            <p class="subtitle">Something went wrong on our end. Please try again later.</p>
            <a href="${pageContext.request.contextPath}/login" class="btn-primary" style="margin-top:1rem;">
                <i class="fas fa-home"></i> Go Home
            </a>
        </div>
    </div>
</body>
</html>

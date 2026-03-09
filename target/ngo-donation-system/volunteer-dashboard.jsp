<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.ngo.dao.*, com.ngo.model.*, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
        <c:set var="pageTitle" value="Volunteer Portal" />
        <%@ include file="/WEB-INF/header.jspf" %>

            <% User user=(User) session.getAttribute("user"); if (user==null || !"Volunteer".equals(user.getRole())) {
                response.sendRedirect("login"); return; } RequirementDAO rDao=new RequirementDAO(); List<Requirement>
                reqs = rDao.getPendingRequirements();
                request.setAttribute("reqs", reqs);

                DistributionDAO distDao = new DistributionDAO();
                List<DistributionLog> myDist = distDao.getDistributionsByVolunteer(user.getUserId());
                    request.setAttribute("myDist", myDist);
                    %>

                    <div class="page-header">
                        <h1><i class="fas fa-truck"></i> Volunteer Dispatch Center</h1>
                        <p>Log field distributions and track your impact, ${fn:escapeXml(sessionScope.user.fullName)}.
                        </p>
                    </div>

                    <!-- Stats -->
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon blue"><i class="fas fa-truck-loading"></i></div>
                            <div class="stat-value">${fn:length(myDist)}</div>
                            <div class="stat-label">My Distributions</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon amber"><i class="fas fa-clipboard-list"></i></div>
                            <div class="stat-value">${fn:length(reqs)}</div>
                            <div class="stat-label">Pending Requirements</div>
                        </div>
                    </div>

                    <c:if test="${param.success == '1'}">
                        <div class="alert alert-success"><i class="fas fa-check-circle"></i> Distribution logged
                            successfully. Inventory updated via trigger.</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${fn:escapeXml(error)}
                        </div>
                    </c:if>

                    <div class="content-grid">
                        <!-- Distribution Form -->
                        <div class="glass-card" style="height:fit-content;">
                            <h3 class="section-title"><i class="fas fa-shipping-fast"></i> Log New Distribution</h3>
                            <form action="distribute" method="post">
                                <div class="form-group">
                                    <label for="requirementId"><i class="fas fa-map-marker-alt"></i> Requirement
                                        (Destination)</label>
                                    <select id="requirementId" name="requirementId" required>
                                        <option value="">-- Select Target Requirement --</option>
                                        <c:forEach var="r" items="${reqs}">
                                            <option value="${r.requirementId}">
                                                [${r.urgency}] ${fn:escapeXml(r.location)} - ${fn:escapeXml(r.itemName)}
                                                (need ${r.quantityNeeded})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="quantity"><i class="fas fa-sort-numeric-up"></i> Quantity to
                                        Distribute</label>
                                    <input type="number" id="quantity" name="quantity" required min="1" max="100000"
                                        step="1" placeholder="Amount to deliver">
                                </div>

                                <div class="form-group">
                                    <label for="notes"><i class="fas fa-sticky-note"></i> Field Notes (optional)</label>
                                    <textarea id="notes" name="notes"
                                        placeholder="Conditions on ground, difficulties, etc." maxlength="500"
                                        rows="2"></textarea>
                                </div>

                                <div class="alert alert-info" style="margin-bottom:1rem;">
                                    <i class="fas fa-info-circle"></i>
                                    <span>Submitting triggers automatic inventory deduction via database trigger
                                        (<code>trg_after_distribution_insert</code>).</span>
                                </div>

                                <button type="submit" class="btn-primary" style="width:100%;">
                                    <i class="fas fa-check-circle"></i> Confirm Distribution
                                </button>
                            </form>
                        </div>

                        <!-- Pending Requirements -->
                        <div class="glass-card">
                            <h3 class="section-title"><i class="fas fa-exclamation-triangle"></i> Pending Requirements
                            </h3>
                            <c:choose>
                                <c:when test="${empty reqs}">
                                    <div class="empty-state"><i class="fas fa-check-circle"></i>
                                        <p>All requirements fulfilled!</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="scroll-area">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th>Location</th>
                                                    <th>Item</th>
                                                    <th>Needed</th>
                                                    <th>Fulfilled</th>
                                                    <th>Urgency</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="r" items="${reqs}">
                                                    <tr>
                                                        <td>${fn:escapeXml(r.location)}</td>
                                                        <td><strong>${fn:escapeXml(r.itemName)}</strong></td>
                                                        <td>${r.quantityNeeded}</td>
                                                        <td>${r.quantityFulfilled}</td>
                                                        <td><span
                                                                class="badge badge-${fn:toLowerCase(r.urgency)}">${r.urgency}</span>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- My Distribution History -->
                    <div class="glass-card" style="margin-top:1.5rem;">
                        <h3 class="section-title"><i class="fas fa-history"></i> My Distribution History</h3>
                        <c:choose>
                            <c:when test="${empty myDist}">
                                <div class="empty-state"><i class="fas fa-truck"></i>
                                    <p>No distributions logged yet.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="scroll-area">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Date</th>
                                                <th>Location</th>
                                                <th>Item</th>
                                                <th>Qty</th>
                                                <th>Notes</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="d" items="${myDist}">
                                                <tr>
                                                    <td>
                                                        <fmt:formatDate value="${d.distributedAt}"
                                                            pattern="dd MMM yyyy" />
                                                    </td>
                                                    <td>${fn:escapeXml(d.location)}</td>
                                                    <td>${fn:escapeXml(d.itemName)}</td>
                                                    <td>${d.quantityDistributed}</td>
                                                    <td
                                                        style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
                                                        ${fn:escapeXml(d.notes)}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%@ include file="/WEB-INF/footer.jspf" %>
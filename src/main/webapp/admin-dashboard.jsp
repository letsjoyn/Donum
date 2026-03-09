<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ngo.dao.*, com.ngo.model.*, com.ngo.util.MatchingAlgorithm, java.util.*" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="/WEB-INF/header.jspf" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equals(user.getRole())) { response.sendRedirect("login"); return; }

    DashboardDAO dashDao = new DashboardDAO();
    Map<String, Object> stats = dashDao.getAdminStats();
    request.setAttribute("stats", stats);

    InventoryDAO iDao = new InventoryDAO();
    List<InventoryItem> inventory = iDao.getAllInventory();
    List<InventoryItem> lowStock = iDao.getLowStockItems();
    request.setAttribute("inventory", inventory);
    request.setAttribute("lowStock", lowStock);

    RequirementDAO rDao = new RequirementDAO();
    List<Requirement> reqs = rDao.getPendingRequirements();
    request.setAttribute("reqs", reqs);

    DonationDAO dDao = new DonationDAO();
    List<Donation> donations = dDao.getAllDonations();
    request.setAttribute("donations", donations);

    DistributionDAO distDao = new DistributionDAO();
    List<DistributionLog> recent = distDao.getRecentDistributions(10);
    request.setAttribute("recent", recent);

    CampaignDAO cDao = new CampaignDAO();
    List<Campaign> campaigns = cDao.getActiveCampaigns();
    request.setAttribute("campaigns", campaigns);

    List<Map<String, Object>> plan = MatchingAlgorithm.generateDistributionPlan(reqs, inventory);
    request.setAttribute("plan", plan);
%>

<div class="main-content">
    <!-- Page Header -->
    <div class="page-header">
        <h1><i class="fas fa-shield-alt"></i> Admin Control Center</h1>
        <p>Welcome back, ${fn:escapeXml(sessionScope.user.fullName)}. Here's your system overview.</p>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon blue"><i class="fas fa-hand-holding-usd"></i></div>
            <div class="stat-value">${stats.totalDonations}</div>
            <div class="stat-label">Total Donations</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon green"><i class="fas fa-rupee-sign"></i></div>
            <div class="stat-value"><fmt:formatNumber value="${stats.totalCash}" type="currency" currencySymbol="₹" maxFractionDigits="0"/></div>
            <div class="stat-label">Cash Raised</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon purple"><i class="fas fa-truck"></i></div>
            <div class="stat-value">${stats.totalDistributions}</div>
            <div class="stat-label">Distributions</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon amber"><i class="fas fa-exclamation-triangle"></i></div>
            <div class="stat-value">${stats.pendingRequirements}</div>
            <div class="stat-label">Pending Requirements</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon red"><i class="fas fa-battery-quarter"></i></div>
            <div class="stat-value">${fn:length(lowStock)}</div>
            <div class="stat-label">Low Stock Items</div>
        </div>
    </div>

    <!-- Charts Row -->
    <div class="content-grid" style="margin-bottom: 1.5rem;">
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-chart-line"></i> Donation Trends</h3>
            <div class="chart-container"><canvas id="chartTrends"></canvas></div>
        </div>
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-chart-pie"></i> Donation Types</h3>
            <div class="chart-container"><canvas id="chartTypes"></canvas></div>
        </div>
    </div>

    <div class="content-grid" style="margin-bottom: 1.5rem;">
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-chart-bar"></i> Urgency Distribution</h3>
            <div class="chart-container"><canvas id="chartUrgency"></canvas></div>
        </div>
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-bullhorn"></i> Campaign Progress</h3>
            <div class="chart-container"><canvas id="chartCampaigns"></canvas></div>
        </div>
    </div>

    <!-- AI Matching Plan -->
    <div class="glass-card" style="margin-bottom: 1.5rem;">
        <h3 class="section-title"><i class="fas fa-brain"></i> AI Distribution Plan (Priority-Weighted)</h3>
        <c:choose>
            <c:when test="${empty plan}">
                <div class="empty-state"><i class="fas fa-check-circle"></i><p>No pending requirements to match.</p></div>
            </c:when>
            <c:otherwise>
                <div class="scroll-area">
                    <table>
                        <thead><tr><th>Location</th><th>Item</th><th>Needed</th><th>Available</th><th>Allocate</th><th>Match</th><th>Score</th></tr></thead>
                        <tbody>
                            <c:forEach var="p" items="${plan}">
                                <tr>
                                    <td>${fn:escapeXml(p.location)}</td>
                                    <td><strong>${fn:escapeXml(p.itemName)}</strong></td>
                                    <td>${p.needed}</td>
                                    <td>${p.available}</td>
                                    <td>${p.allocate}</td>
                                    <td><span class="match-${fn:toLowerCase(p.matchType)}">${p.matchType}</span></td>
                                    <td>${p.score}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Requirement + Inventory -->
    <div class="content-grid">
        <!-- Requirements -->
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-clipboard-list"></i> Pending Requirements</h3>
            <div class="scroll-area">
                <table>
                    <thead><tr><th>Location</th><th>Item</th><th>Needed</th><th>Urgency</th><th>Campaign</th></tr></thead>
                    <tbody>
                        <c:forEach var="r" items="${reqs}">
                            <tr>
                                <td>${fn:escapeXml(r.location)}</td>
                                <td><strong>${fn:escapeXml(r.itemName)}</strong></td>
                                <td>${r.quantityNeeded}</td>
                                <td><span class="badge badge-${fn:toLowerCase(r.urgency)}">${r.urgency}</span></td>
                                <td>${fn:escapeXml(r.campaignName)}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reqs}">
                            <tr><td colspan="5" class="empty-state"><p>No pending requirements.</p></td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Inventory -->
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-boxes"></i> Inventory Overview</h3>
            <div class="scroll-area">
                <table>
                    <thead><tr><th>Item</th><th>Category</th><th>Qty</th><th>Warehouse</th><th>Status</th></tr></thead>
                    <tbody>
                        <c:forEach var="item" items="${inventory}">
                            <tr>
                                <td><strong>${fn:escapeXml(item.itemName)}</strong></td>
                                <td>${fn:escapeXml(item.category)}</td>
                                <td>${item.quantity} ${fn:escapeXml(item.unit)}</td>
                                <td>${fn:escapeXml(item.warehouseName)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.stockStatus == 'Critical'}"><span class="badge badge-critical">${item.stockStatus}</span></c:when>
                                        <c:when test="${item.stockStatus == 'Low'}"><span class="badge badge-high">${item.stockStatus}</span></c:when>
                                        <c:otherwise><span class="badge badge-low">OK</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Add Requirement Form -->
    <div class="content-grid" style="margin-top: 1.5rem;">
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-plus-circle"></i> Add Requirement</h3>
            <form action="requirements" method="post">
                <div class="form-group">
                    <label for="itemName">Item Name</label>
                    <input type="text" id="itemName" name="itemName" required placeholder="e.g. Rice, Blankets" maxlength="100">
                </div>
                <div class="form-group">
                    <label for="quantityNeeded">Quantity Needed</label>
                    <input type="number" id="quantityNeeded" name="quantityNeeded" required min="1" max="100000">
                </div>
                <div class="form-group">
                    <label for="location">Location</label>
                    <input type="text" id="location" name="location" required placeholder="Area / District" maxlength="100">
                </div>
                <div class="form-group">
                    <label for="urgency">Urgency</label>
                    <select id="urgency" name="urgency" required>
                        <option value="Critical">Critical</option>
                        <option value="High">High</option>
                        <option value="Medium" selected>Medium</option>
                        <option value="Low">Low</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="campaignId">Campaign (optional)</label>
                    <select id="campaignId" name="campaignId">
                        <option value="">-- None --</option>
                        <c:forEach var="c" items="${campaigns}">
                            <option value="${c.campaignId}">${fn:escapeXml(c.name)}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn-primary"><i class="fas fa-plus"></i> Add Requirement</button>
            </form>
        </div>

        <!-- Recent Distributions -->
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-truck"></i> Recent Distributions</h3>
            <div class="scroll-area">
                <table>
                    <thead><tr><th>Date</th><th>Volunteer</th><th>Location</th><th>Item</th><th>Qty</th></tr></thead>
                    <tbody>
                        <c:forEach var="d" items="${recent}">
                            <tr>
                                <td><fmt:formatDate value="${d.distributedAt}" pattern="dd MMM yyyy"/></td>
                                <td>${fn:escapeXml(d.volunteerName)}</td>
                                <td>${fn:escapeXml(d.location)}</td>
                                <td>${fn:escapeXml(d.itemName)}</td>
                                <td>${d.quantity}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recent}">
                            <tr><td colspan="5" class="empty-state"><p>No distributions yet.</p></td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>
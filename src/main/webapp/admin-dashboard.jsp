<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.ngo.dao.*, com.ngo.model.*, com.ngo.util.MatchingAlgorithm, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
        <c:set var="pageTitle" value="Admin Dashboard" />
        <%@ include file="/WEB-INF/header.jspf" %>

            <% User user=(User) session.getAttribute("user"); if (user==null || !"Admin".equals(user.getRole())) {
                response.sendRedirect("login"); return; } DashboardDAO dashDao=new DashboardDAO(); Map<String, Object>
                stats = dashDao.getAdminStats();
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

                                        List<Map<String, Object>> plan = MatchingAlgorithm.generateDistributionPlan();
                                            request.setAttribute("plan", plan);
                                            %>

                                            <!-- Page Header -->
                                            <div class="page-header">
                                                <h1><i class="fas fa-shield-alt"></i> Admin Control Center</h1>
                                                <p>Welcome back, ${fn:escapeXml(sessionScope.user.fullName)}. Data is
                                                    loaded live from MySQL on every page refresh.</p>
                                            </div>

                                            <c:if test="${param.deleted == 'donation'}">
                                                <div class="alert alert-success"><i class="fas fa-check-circle"></i>
                                                    Donation deleted. Inventory and campaign totals were adjusted in the
                                                    database.</div>
                                            </c:if>
                                            <c:if test="${param.deleted == 'requirement'}">
                                                <div class="alert alert-success"><i class="fas fa-check-circle"></i>
                                                    Requirement deleted from the database.</div>
                                            </c:if>
                                            <c:if test="${param.deleted == 'distribution'}">
                                                <div class="alert alert-success"><i class="fas fa-check-circle"></i>
                                                    Distribution deleted. Stock and requirement fulfillment were reversed.
                                                </div>
                                            </c:if>
                                            <c:if test="${param.error == 'delete_failed'}">
                                                <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i>
                                                    Could not delete that record. It may already be removed.</div>
                                            </c:if>

                                            <!-- Stats Grid -->
                                            <div class="stats-grid">
                                                <div class="stat-card">
                                                    <div class="stat-icon blue"><i class="fas fa-hand-holding-usd"></i>
                                                    </div>
                                                    <div class="stat-value">${stats.totalDonations}</div>
                                                    <div class="stat-label">Total Donations</div>
                                                </div>
                                                <div class="stat-card">
                                                    <div class="stat-icon green"><i class="fas fa-rupee-sign"></i></div>
                                                    <div class="stat-value">
                                                        <fmt:formatNumber value="${stats.totalCashRaised}"
                                                            type="currency" currencySymbol="₹" maxFractionDigits="0" />
                                                    </div>
                                                    <div class="stat-label">Cash Raised</div>
                                                </div>
                                                <div class="stat-card">
                                                    <div class="stat-icon purple"><i class="fas fa-truck"></i></div>
                                                    <div class="stat-value">${stats.totalDistributions}</div>
                                                    <div class="stat-label">Distributions</div>
                                                </div>
                                                <div class="stat-card">
                                                    <div class="stat-icon amber"><i
                                                            class="fas fa-exclamation-triangle"></i></div>
                                                    <div class="stat-value">${stats.pendingRequirements}</div>
                                                    <div class="stat-label">Pending Requirements</div>
                                                </div>
                                                <div class="stat-card">
                                                    <div class="stat-icon red"><i class="fas fa-battery-quarter"></i>
                                                    </div>
                                                    <div class="stat-value">${fn:length(lowStock)}</div>
                                                    <div class="stat-label">Low Stock Items</div>
                                                </div>
                                            </div>

                                            <div style="display:flex;justify-content:flex-end;margin-bottom:1rem;gap:0.75rem;flex-wrap:wrap;">
                                                <button type="button" id="refreshChartsBtn" class="btn-primary"
                                                    style="padding:0.5rem 1rem;">
                                                    <i class="fas fa-sync-alt"></i> Refresh Charts
                                                </button>
                                                <a href="admin-dashboard.jsp" class="btn-primary"
                                                    style="padding:0.5rem 1rem;text-decoration:none;background:rgba(255,255,255,0.08);">
                                                    <i class="fas fa-redo"></i> Reload Page (stats + tables)
                                                </a>
                                            </div>
                                            <p style="color:#94a3b8;font-size:0.85rem;margin:-0.5rem 0 1rem 0;">
                                                Charts load live from MySQL. After a donor donates, click <strong>Refresh
                                                    Charts</strong> or reload this page.</p>

                                            <!-- Charts Row -->
                                            <div class="content-grid" style="margin-bottom: 1.5rem;">
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-chart-line"></i> Donation
                                                        Trends</h3>
                                                    <div class="chart-container"><canvas id="chartTrends"></canvas>
                                                    </div>
                                                </div>
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-chart-pie"></i> Donation
                                                        Types</h3>
                                                    <div class="chart-container"><canvas id="chartTypes"></canvas></div>
                                                </div>
                                            </div>

                                            <div class="content-grid" style="margin-bottom: 1.5rem;">
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-chart-bar"></i> Urgency
                                                        Distribution</h3>
                                                    <div class="chart-container"><canvas id="chartUrgency"></canvas>
                                                    </div>
                                                </div>
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-bullhorn"></i> Campaign
                                                        Progress</h3>
                                                    <div class="chart-container"><canvas id="chartCampaigns"></canvas>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- All Donations (live from DB) -->
                                            <div class="glass-card" style="margin-bottom: 1.5rem;">
                                                <h3 class="section-title"><i class="fas fa-hand-holding-heart"></i> All
                                                    Donations <span class="role-badge role-donor"
                                                        style="margin-left:0.5rem;">${fn:length(donations)} records</span>
                                                </h3>
                                                <p style="color:var(--text-dim,#94a3b8);font-size:0.9rem;margin-bottom:1rem;">
                                                    New donor registrations and donations appear here after you refresh
                                                    this page. In-kind gifts also update <strong>Inventory</strong> via
                                                    database trigger.</p>
                                                <div class="scroll-area">
                                                    <table>
                                                        <thead>
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Date</th>
                                                                <th>Donor</th>
                                                                <th>Type</th>
                                                                <th>Item / Amount</th>
                                                                <th>Campaign</th>
                                                                <th>Status</th>
                                                                <th>Action</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="don" items="${donations}">
                                                                <tr>
                                                                    <td>#${don.donationId}</td>
                                                                    <td>
                                                                        <fmt:formatDate value="${don.donationDate}"
                                                                            pattern="dd MMM yyyy HH:mm" />
                                                                    </td>
                                                                    <td>${fn:escapeXml(don.donorName)}</td>
                                                                    <td><span
                                                                            class="badge badge-${fn:toLowerCase(don.type)}">${don.type}</span>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when test="${don.type == 'Kind'}">
                                                                                ${fn:escapeXml(don.itemName)} ×
                                                                                ${don.amountOrQuantity}</c:when>
                                                                            <c:otherwise>₹<fmt:formatNumber
                                                                                    value="${don.amountOrQuantity}"
                                                                                    maxFractionDigits="0" /></c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>${empty don.campaignName ? 'General' : fn:escapeXml(don.campaignName)}</td>
                                                                    <td><span
                                                                            class="badge badge-${fn:toLowerCase(don.status)}">${don.status}</span>
                                                                    </td>
                                                                    <td>
                                                                        <form action="admin/delete" method="post"
                                                                            style="display:inline;"
                                                                            onsubmit="return confirm('Delete donation #${don.donationId}? This reverses inventory/campaign totals.');">
                                                                            <input type="hidden" name="entity"
                                                                                value="donation" />
                                                                            <input type="hidden" name="id"
                                                                                value="${don.donationId}" />
                                                                            <button type="submit" class="btn-danger"
                                                                                style="padding:0.35rem 0.65rem;font-size:0.8rem;">
                                                                                <i class="fas fa-trash"></i> Delete
                                                                            </button>
                                                                        </form>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                            <c:if test="${empty donations}">
                                                                <tr>
                                                                    <td colspan="8" class="empty-state">
                                                                        <p>No donations in database yet.</p>
                                                                    </td>
                                                                </tr>
                                                            </c:if>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>

                                            <!-- AI Matching Plan -->
                                            <div class="glass-card" style="margin-bottom: 1.5rem;">
                                                <h3 class="section-title"><i class="fas fa-brain"></i> AI Distribution
                                                    Plan (Priority-Weighted)</h3>
                                                <c:choose>
                                                    <c:when test="${empty plan}">
                                                        <div class="empty-state"><i class="fas fa-check-circle"></i>
                                                            <p>No pending requirements to match.</p>
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
                                                                        <th>Available</th>
                                                                        <th>Allocate</th>
                                                                        <th>Match</th>
                                                                        <th>Score</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <c:forEach var="p" items="${plan}">
                                                                        <tr>
                                                                            <td>${fn:escapeXml(p.location)}</td>
                                                                            <td><strong>${fn:escapeXml(p.itemName)}</strong>
                                                                            </td>
                                                                            <td>${p.quantityNeeded}</td>
                                                                            <td>${p.availableStock}</td>
                                                                            <td>${p.canFulfill}</td>
                                                                            <td><span
                                                                                    class="match-${fn:toLowerCase(p.matchType)}">${p.matchType}</span>
                                                                            </td>
                                                                            <td>${p.priorityScore}</td>
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
                                                    <h3 class="section-title"><i class="fas fa-clipboard-list"></i>
                                                        Pending Requirements</h3>
                                                    <div class="scroll-area">
                                                        <table>
                                                            <thead>
                                                                <tr>
                                                                    <th>Location</th>
                                                                    <th>Item</th>
                                                                    <th>Needed</th>
                                                                    <th>Urgency</th>
                                                                    <th>Campaign</th>
                                                                    <th>Action</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="r" items="${reqs}">
                                                                    <tr>
                                                                        <td>${fn:escapeXml(r.location)}</td>
                                                                        <td><strong>${fn:escapeXml(r.itemName)}</strong>
                                                                        </td>
                                                                        <td>${r.quantityNeeded}</td>
                                                                        <td><span
                                                                                class="badge badge-${fn:toLowerCase(r.urgency)}">${r.urgency}</span>
                                                                        </td>
                                                                        <td>${fn:escapeXml(r.campaignName)}</td>
                                                                        <td>
                                                                            <form action="admin/delete" method="post"
                                                                                style="display:inline;"
                                                                                onsubmit="return confirm('Delete this requirement?');">
                                                                                <input type="hidden" name="entity"
                                                                                    value="requirement" />
                                                                                <input type="hidden" name="id"
                                                                                    value="${r.requirementId}" />
                                                                                <button type="submit" class="btn-danger"
                                                                                    style="padding:0.35rem 0.65rem;font-size:0.8rem;">
                                                                                    <i class="fas fa-trash"></i>
                                                                                </button>
                                                                            </form>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                                <c:if test="${empty reqs}">
                                                                    <tr>
                                                                        <td colspan="6" class="empty-state">
                                                                            <p>No pending requirements.</p>
                                                                        </td>
                                                                    </tr>
                                                                </c:if>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>

                                                <!-- Inventory -->
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-boxes"></i> Inventory
                                                        Overview</h3>
                                                    <div class="scroll-area">
                                                        <table>
                                                            <thead>
                                                                <tr>
                                                                    <th>Item</th>
                                                                    <th>Category</th>
                                                                    <th>Qty</th>
                                                                    <th>Warehouse</th>
                                                                    <th>Status</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="item" items="${inventory}">
                                                                    <tr>
                                                                        <td><strong>${fn:escapeXml(item.itemName)}</strong>
                                                                        </td>
                                                                        <td>${fn:escapeXml(item.category)}</td>
                                                                        <td>${item.quantity} ${fn:escapeXml(item.unit)}
                                                                        </td>
                                                                        <td>${fn:escapeXml(item.warehouseName)}</td>
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${item.stockStatus == 'OUT_OF_STOCK'}">
                                                                                    <span
                                                                                        class="badge badge-critical">OUT
                                                                                        OF STOCK</span></c:when>
                                                                                <c:when
                                                                                    test="${item.stockStatus == 'CRITICAL'}">
                                                                                    <span
                                                                                        class="badge badge-critical">${item.stockStatus}</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${item.stockStatus == 'LOW'}">
                                                                                    <span
                                                                                        class="badge badge-high">${item.stockStatus}</span>
                                                                                </c:when>
                                                                                <c:otherwise><span
                                                                                        class="badge badge-low">ADEQUATE</span>
                                                                                </c:otherwise>
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
                                                    <h3 class="section-title"><i class="fas fa-plus-circle"></i> Add
                                                        Requirement</h3>
                                                    <form action="requirements" method="post">
                                                        <div class="form-group">
                                                            <label for="itemName">Item Name</label>
                                                            <input type="text" id="itemName" name="itemName" required
                                                                placeholder="e.g. Rice, Blankets" maxlength="100">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="quantityNeeded">Quantity Needed</label>
                                                            <input type="number" id="quantityNeeded"
                                                                name="quantityNeeded" required min="1" max="100000">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="location">Location</label>
                                                            <input type="text" id="location" name="location" required
                                                                placeholder="Area / District" maxlength="100">
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
                                                            <label for="description">Description (optional)</label>
                                                            <textarea id="description" name="description"
                                                                placeholder="Brief description of the need"
                                                                maxlength="500" rows="2"></textarea>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="campaignId">Campaign (optional)</label>
                                                            <select id="campaignId" name="campaignId">
                                                                <option value="">-- None --</option>
                                                                <c:forEach var="c" items="${campaigns}">
                                                                    <option value="${c.campaignId}">
                                                                        ${fn:escapeXml(c.name)}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                        <button type="submit" class="btn-primary"><i
                                                                class="fas fa-plus"></i> Add Requirement</button>
                                                    </form>
                                                </div>

                                                <!-- Recent Distributions -->
                                                <div class="glass-card">
                                                    <h3 class="section-title"><i class="fas fa-truck"></i> Recent
                                                        Distributions</h3>
                                                    <div class="scroll-area">
                                                        <table>
                                                            <thead>
                                                                <tr>
                                                                    <th>Date</th>
                                                                    <th>Volunteer</th>
                                                                    <th>Location</th>
                                                                    <th>Item</th>
                                                                    <th>Qty</th>
                                                                    <th>Action</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="d" items="${recent}">
                                                                    <tr>
                                                                        <td>
                                                                            <fmt:formatDate value="${d.distributedAt}"
                                                                                pattern="dd MMM yyyy" />
                                                                        </td>
                                                                        <td>${fn:escapeXml(d.volunteerName)}</td>
                                                                        <td>${fn:escapeXml(d.location)}</td>
                                                                        <td>${fn:escapeXml(d.itemName)}</td>
                                                                        <td>${d.quantityDistributed}</td>
                                                                        <td>
                                                                            <form action="admin/delete" method="post"
                                                                                style="display:inline;"
                                                                                onsubmit="return confirm('Delete this distribution? Stock will be restored.');">
                                                                                <input type="hidden" name="entity"
                                                                                    value="distribution" />
                                                                                <input type="hidden" name="id"
                                                                                    value="${d.logId}" />
                                                                                <button type="submit" class="btn-danger"
                                                                                    style="padding:0.35rem 0.65rem;font-size:0.8rem;">
                                                                                    <i class="fas fa-trash"></i>
                                                                                </button>
                                                                            </form>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                                <c:if test="${empty recent}">
                                                                    <tr>
                                                                        <td colspan="6" class="empty-state">
                                                                            <p>No distributions yet.</p>
                                                                        </td>
                                                                    </tr>
                                                                </c:if>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>

                                            <%@ include file="/WEB-INF/footer.jspf" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ngo.dao.*, com.ngo.model.*, java.util.List" %>
<c:set var="pageTitle" value="Donor Dashboard" />
<%@ include file="/WEB-INF/header.jspf" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Donor".equals(user.getRole())) { response.sendRedirect("login"); return; }

    DonationDAO dDao = new DonationDAO();
    List<Donation> myDonations = dDao.getDonationsByDonor(user.getUserId());
    request.setAttribute("myDonations", myDonations);

    CampaignDAO cDao = new CampaignDAO();
    List<Campaign> campaigns = cDao.getActiveCampaigns();
    request.setAttribute("campaigns", campaigns);
%>

<div class="main-content">
    <div class="page-header">
        <h1><i class="fas fa-heart"></i> Welcome, ${fn:escapeXml(sessionScope.user.fullName)}</h1>
        <p>Thank you for your generosity. Every contribution makes a difference.</p>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon green"><i class="fas fa-hand-holding-usd"></i></div>
            <div class="stat-value">${fn:length(myDonations)}</div>
            <div class="stat-label">My Donations</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon blue"><i class="fas fa-bullhorn"></i></div>
            <div class="stat-value">${fn:length(campaigns)}</div>
            <div class="stat-label">Active Campaigns</div>
        </div>
    </div>

    <c:if test="${param.success == '1'}">
        <div class="alert alert-success"><i class="fas fa-check-circle"></i> Thank you! Your donation was recorded successfully.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${fn:escapeXml(error)}</div>
    </c:if>

    <div class="content-grid sidebar">
        <!-- Donation Form -->
        <div class="glass-card" style="height:fit-content;">
            <h3 class="section-title"><i class="fas fa-plus-circle"></i> Make a Donation</h3>
            <form action="donate" method="post">
                <div class="form-group">
                    <label for="donationType"><i class="fas fa-tags"></i> Type</label>
                    <select id="donationType" name="type" required onchange="toggleDonationFields()">
                        <option value="Cash">Cash Transfer</option>
                        <option value="Kind">In-Kind (Goods)</option>
                    </select>
                </div>

                <div id="kindSection" style="display:none;">
                    <div class="form-group">
                        <label for="itemName"><i class="fas fa-box"></i> Item Name</label>
                        <input type="text" id="itemName" name="itemName" placeholder="e.g. Rice, Blankets, Medicines" maxlength="100">
                    </div>
                </div>

                <div id="cashSection">
                    <div class="form-group">
                        <label for="amount"><i class="fas fa-rupee-sign"></i> Amount / Quantity</label>
                        <input type="number" id="amount" name="amount" required min="1" max="100000000" step="0.01">
                    </div>
                </div>

                <div class="form-group">
                    <label for="campaignId"><i class="fas fa-bullhorn"></i> Campaign (optional)</label>
                    <select id="campaignId" name="campaignId">
                        <option value="">-- General Donation --</option>
                        <c:forEach var="c" items="${campaigns}">
                            <option value="${c.campaignId}">${fn:escapeXml(c.name)}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="notes"><i class="fas fa-sticky-note"></i> Notes (optional)</label>
                    <textarea id="notes" name="notes" placeholder="Any special instructions..." maxlength="500" rows="2"></textarea>
                </div>

                <button type="submit" class="btn-primary" style="width:100%;">
                    <i class="fas fa-heart"></i> Donate Now
                </button>
            </form>
        </div>

        <!-- Donation History -->
        <div class="glass-card">
            <h3 class="section-title"><i class="fas fa-history"></i> My Donation History</h3>
            <c:choose>
                <c:when test="${empty myDonations}">
                    <div class="empty-state">
                        <i class="fas fa-gift"></i>
                        <p>No donations yet. Make your first contribution today!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="scroll-area">
                        <table>
                            <thead><tr><th>Date</th><th>Type</th><th>Details</th><th>Campaign</th><th>Status</th><th>Receipt</th></tr></thead>
                            <tbody>
                                <c:forEach var="d" items="${myDonations}">
                                    <tr>
                                        <td><fmt:formatDate value="${d.donationDate}" pattern="dd MMM yyyy"/></td>
                                        <td><span class="badge badge-${fn:toLowerCase(d.type)}">${d.type}</span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${d.type == 'Cash'}"><strong>&#8377;${d.amountOrQuantity}</strong></c:when>
                                                <c:otherwise>${d.amountOrQuantity} x ${fn:escapeXml(d.itemName)}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${fn:escapeXml(d.campaignName)}</td>
                                        <td><span class="badge badge-${fn:toLowerCase(d.status)}">${d.status}</span></td>
                                        <td>
                                            <a href="generateReceipt?id=${d.donationId}&type=${d.type}&amount=${d.amountOrQuantity}"
                                               class="btn-primary btn-sm" title="Download PDF Receipt">
                                                <i class="fas fa-file-pdf"></i>
                                            </a>
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

    <!-- Active Campaigns -->
    <c:if test="${not empty campaigns}">
        <div style="margin-top: 1.5rem;">
            <h3 class="section-title" style="font-size:1.2rem;"><i class="fas fa-bullhorn"></i> Active Campaigns</h3>
            <div class="campaign-grid">
                <c:forEach var="c" items="${campaigns}">
                    <div class="campaign-card">
                        <div class="card-header">
                            <h3>${fn:escapeXml(c.name)}</h3>
                            <span class="org-name">${fn:escapeXml(c.orgName)}</span>
                        </div>
                        <div class="card-body">
                            <p style="font-size:0.85rem;color:var(--text-dim);margin-bottom:0.5rem;">${fn:escapeXml(c.description)}</p>
                            <div class="amounts">
                                <span class="raised">&#8377;<fmt:formatNumber value="${c.raisedAmount}" maxFractionDigits="0"/></span>
                                <span class="target">of &#8377;<fmt:formatNumber value="${c.targetAmount}" maxFractionDigits="0"/></span>
                            </div>
                            <div class="progress-bar"><div class="progress-fill" style="width:${c.progressPercent}%"></div></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>
</div>

<%@ include file="/WEB-INF/footer.jspf" %>
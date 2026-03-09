<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.ngo.dao.CampaignDAO, com.ngo.model.Campaign, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
        <c:set var="pageTitle" value="Campaigns" />
        <%@ include file="/WEB-INF/header.jspf" %>

            <% CampaignDAO cDao=new CampaignDAO(); List<Campaign> campaigns = cDao.getAllCampaigns();
                request.setAttribute("campaigns", campaigns);
                %>

                <div class="main-content">
                    <div class="page-header">
                        <h1><i class="fas fa-bullhorn"></i> Relief Campaigns</h1>
                        <p>Browse active campaigns and see how donations are making an impact.</p>
                    </div>

                    <c:choose>
                        <c:when test="${empty campaigns}">
                            <div class="glass-card">
                                <div class="empty-state">
                                    <i class="fas fa-bullhorn"></i>
                                    <p>No campaigns available at the moment.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="campaign-grid">
                                <c:forEach var="c" items="${campaigns}">
                                    <div class="campaign-card">
                                        <div class="card-header">
                                            <div style="display:flex;justify-content:space-between;align-items:center;">
                                                <div>
                                                    <h3>${fn:escapeXml(c.name)}</h3>
                                                    <span class="org-name"><i class="fas fa-building"></i>
                                                        ${fn:escapeXml(c.orgName)}</span>
                                                </div>
                                                <span class="badge badge-${fn:toLowerCase(c.status)}">${c.status}</span>
                                            </div>
                                        </div>
                                        <div class="card-body">
                                            <p
                                                style="font-size:0.85rem;color:var(--text-dim);margin-bottom:1rem;min-height:40px;">
                                                ${fn:escapeXml(c.description)}
                                            </p>

                                            <div
                                                style="display:flex;justify-content:space-between;font-size:0.8rem;color:var(--text-muted);margin-bottom:0.5rem;">
                                                <span><i class="fas fa-calendar-alt"></i>
                                                    <fmt:formatDate value="${c.startDate}" pattern="dd MMM yyyy" />
                                                    <c:if test="${c.endDate != null}"> -
                                                        <fmt:formatDate value="${c.endDate}" pattern="dd MMM yyyy" />
                                                    </c:if>
                                                </span>
                                            </div>

                                            <div class="amounts">
                                                <span class="raised">&#8377;
                                                    <fmt:formatNumber value="${c.raisedAmount}" maxFractionDigits="0" />
                                                    raised
                                                </span>
                                                <span class="target">of &#8377;
                                                    <fmt:formatNumber value="${c.targetAmount}" maxFractionDigits="0" />
                                                </span>
                                            </div>
                                            <div class="progress-bar">
                                                <div class="progress-fill" style="width:${c.progressPercent}%"></div>
                                            </div>
                                            <div
                                                style="text-align:right;font-size:0.75rem;color:var(--text-dim);margin-top:0.3rem;">
                                                ${c.progressPercent}% funded
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%@ include file="/WEB-INF/footer.jspf" %>
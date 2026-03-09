package com.ngo.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Campaign {
    private int campaignId;
    private int orgId;
    private String orgName; // joined field
    private String name;
    private String description;
    private double targetAmount;
    private double raisedAmount;
    private Date startDate;
    private Date endDate;
    private String status;
    private String imageUrl;
    private Timestamp createdAt;

    // Computed
    public double getProgressPercent() {
        if (targetAmount <= 0) return 0;
        return Math.min(100.0, Math.round((raisedAmount / targetAmount) * 1000.0) / 10.0);
    }

    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }
    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getRaisedAmount() { return raisedAmount; }
    public void setRaisedAmount(double raisedAmount) { this.raisedAmount = raisedAmount; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

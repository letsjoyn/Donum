package com.ngo.model;

import java.sql.Timestamp;

public class Requirement {
    private int requirementId;
    private Integer campaignId;
    private String campaignName; // joined field
    private String location;
    private String itemName;
    private double quantityNeeded;
    private double quantityFulfilled;
    private String urgency;
    private String status;
    private String description;
    private Timestamp createdAt;

    public double getFulfillmentPercent() {
        if (quantityNeeded <= 0) return 0;
        return Math.min(100.0, Math.round((quantityFulfilled / quantityNeeded) * 1000.0) / 10.0);
    }

    public int getRequirementId() { return requirementId; }
    public void setRequirementId(int requirementId) { this.requirementId = requirementId; }
    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(double quantityNeeded) { this.quantityNeeded = quantityNeeded; }
    public double getQuantityFulfilled() { return quantityFulfilled; }
    public void setQuantityFulfilled(double quantityFulfilled) { this.quantityFulfilled = quantityFulfilled; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

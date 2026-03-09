package com.ngo.model;

import java.sql.Timestamp;

public class DistributionLog {
    private int logId;
    private int donationId;
    private int requirementId;
    private int volunteerId;
    private String volunteerName; // joined field
    private String location; // joined from requirements
    private String itemName; // joined from requirements
    private double quantityDistributed;
    private String notes;
    private Timestamp distributedAt;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getDonationId() { return donationId; }
    public void setDonationId(int donationId) { this.donationId = donationId; }
    public int getRequirementId() { return requirementId; }
    public void setRequirementId(int requirementId) { this.requirementId = requirementId; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getQuantityDistributed() { return quantityDistributed; }
    public void setQuantityDistributed(double quantityDistributed) { this.quantityDistributed = quantityDistributed; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getDistributedAt() { return distributedAt; }
    public void setDistributedAt(Timestamp distributedAt) { this.distributedAt = distributedAt; }
}

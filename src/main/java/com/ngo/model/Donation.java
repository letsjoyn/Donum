package com.ngo.model;

import java.sql.Timestamp;

public class Donation {
    private int donationId;
    private int donorId;
    private String donorName; // joined field
    private Integer campaignId;
    private String campaignName; // joined field
    private String type;
    private String itemName;
    private double amountOrQuantity;
    private String status;
    private String notes;
    private Timestamp donationDate;

    public int getDonationId() { return donationId; }
    public void setDonationId(int donationId) { this.donationId = donationId; }
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }
    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }
    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getAmountOrQuantity() { return amountOrQuantity; }
    public void setAmountOrQuantity(double amountOrQuantity) { this.amountOrQuantity = amountOrQuantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getDonationDate() { return donationDate; }
    public void setDonationDate(Timestamp donationDate) { this.donationDate = donationDate; }
}

package com.ngo.model;

import java.sql.Timestamp;

public class InventoryItem {
    private int itemId;
    private Integer warehouseId;
    private String warehouseName; // joined field
    private String warehouseLocation; // joined field
    private String itemName;
    private String category;
    private double quantity;
    private String unit;
    private double minThreshold;
    private Timestamp lastUpdated;

    public String getStockStatus() {
        if (quantity <= 0) return "OUT_OF_STOCK";
        if (quantity < minThreshold) return "CRITICAL";
        if (quantity < minThreshold * 2) return "LOW";
        return "ADEQUATE";
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public Integer getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getMinThreshold() { return minThreshold; }
    public void setMinThreshold(double minThreshold) { this.minThreshold = minThreshold; }
    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }
}

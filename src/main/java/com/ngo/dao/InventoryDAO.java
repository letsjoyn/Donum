package com.ngo.dao;

import com.ngo.model.InventoryItem;
import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    private InventoryItem mapRow(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setItemId(rs.getInt("item_id"));
        item.setWarehouseId(rs.getObject("warehouse_id") != null ? rs.getInt("warehouse_id") : null);
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setQuantity(rs.getDouble("quantity"));
        item.setUnit(rs.getString("unit"));
        item.setMinThreshold(rs.getDouble("min_threshold"));
        item.setLastUpdated(rs.getTimestamp("last_updated"));
        return item;
    }

    public List<InventoryItem> getAllInventory() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT i.*, w.name AS warehouse_name, w.location AS warehouse_location " +
                     "FROM inventory i LEFT JOIN warehouses w ON i.warehouse_id = w.warehouse_id " +
                     "ORDER BY i.category, i.item_name";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                InventoryItem item = mapRow(rs);
                item.setWarehouseName(rs.getString("warehouse_name"));
                item.setWarehouseLocation(rs.getString("warehouse_location"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<InventoryItem> getLowStockItems() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT i.*, w.name AS warehouse_name, w.location AS warehouse_location " +
                     "FROM inventory i LEFT JOIN warehouses w ON i.warehouse_id = w.warehouse_id " +
                     "WHERE i.quantity < i.min_threshold ORDER BY i.quantity ASC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                InventoryItem item = mapRow(rs);
                item.setWarehouseName(rs.getString("warehouse_name"));
                item.setWarehouseLocation(rs.getString("warehouse_location"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStock(String itemName, double quantityChange) {
        String sql = "UPDATE inventory SET quantity = quantity + ? WHERE item_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, quantityChange);
            ps.setString(2, itemName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addItem(InventoryItem item) {
        String sql = "INSERT INTO inventory (warehouse_id, item_name, category, quantity, unit, min_threshold) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (item.getWarehouseId() != null) ps.setInt(1, item.getWarehouseId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getCategory());
            ps.setDouble(4, item.getQuantity());
            ps.setString(5, item.getUnit());
            ps.setDouble(6, item.getMinThreshold());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM inventory WHERE quantity < min_threshold";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

package com.ngo.dao;

import com.ngo.model.Requirement;
import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequirementDAO {

    private Requirement mapRow(ResultSet rs) throws SQLException {
        Requirement r = new Requirement();
        r.setRequirementId(rs.getInt("requirement_id"));
        r.setCampaignId(rs.getObject("campaign_id") != null ? rs.getInt("campaign_id") : null);
        r.setLocation(rs.getString("location"));
        r.setItemName(rs.getString("item_name"));
        r.setQuantityNeeded(rs.getDouble("quantity_needed"));
        r.setQuantityFulfilled(rs.getDouble("quantity_fulfilled"));
        r.setUrgency(rs.getString("urgency"));
        r.setStatus(rs.getString("status"));
        r.setDescription(rs.getString("description"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }

    public List<Requirement> getPendingRequirements() {
        List<Requirement> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name AS campaign_name FROM requirements r " +
                     "LEFT JOIN campaigns c ON r.campaign_id = c.campaign_id " +
                     "WHERE r.status != 'Fulfilled' " +
                     "ORDER BY FIELD(r.urgency, 'Critical', 'High', 'Medium', 'Low')";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Requirement r = mapRow(rs);
                r.setCampaignName(rs.getString("campaign_name"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Requirement> getAllRequirements() {
        List<Requirement> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name AS campaign_name FROM requirements r " +
                     "LEFT JOIN campaigns c ON r.campaign_id = c.campaign_id " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Requirement r = mapRow(rs);
                r.setCampaignName(rs.getString("campaign_name"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addRequirement(Requirement r) {
        String sql = "INSERT INTO requirements (campaign_id, location, item_name, quantity_needed, urgency, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (r.getCampaignId() != null) ps.setInt(1, r.getCampaignId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, r.getLocation());
            ps.setString(3, r.getItemName());
            ps.setDouble(4, r.getQuantityNeeded());
            ps.setString(5, r.getUrgency());
            ps.setString(6, r.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countPending() {
        String sql = "SELECT COUNT(*) FROM requirements WHERE status != 'Fulfilled'";
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

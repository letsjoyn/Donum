package com.ngo.dao;

import com.ngo.model.Campaign;
import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CampaignDAO {

    private Campaign mapRow(ResultSet rs) throws SQLException {
        Campaign c = new Campaign();
        c.setCampaignId(rs.getInt("campaign_id"));
        c.setOrgId(rs.getInt("org_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setTargetAmount(rs.getDouble("target_amount"));
        c.setRaisedAmount(rs.getDouble("raised_amount"));
        c.setStartDate(rs.getDate("start_date"));
        c.setEndDate(rs.getDate("end_date"));
        c.setStatus(rs.getString("status"));
        c.setImageUrl(rs.getString("image_url"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    public List<Campaign> getActiveCampaigns() {
        List<Campaign> list = new ArrayList<>();
        String sql = "SELECT c.*, o.org_name FROM campaigns c " +
                     "JOIN organizations o ON c.org_id = o.org_id " +
                     "WHERE c.status = 'Active' ORDER BY c.end_date ASC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Campaign c = mapRow(rs);
                c.setOrgName(rs.getString("org_name"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Campaign> getAllCampaigns() {
        List<Campaign> list = new ArrayList<>();
        String sql = "SELECT c.*, o.org_name FROM campaigns c " +
                     "JOIN organizations o ON c.org_id = o.org_id " +
                     "ORDER BY c.created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Campaign c = mapRow(rs);
                c.setOrgName(rs.getString("org_name"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Campaign getById(int id) {
        String sql = "SELECT c.*, o.org_name FROM campaigns c " +
                     "JOIN organizations o ON c.org_id = o.org_id WHERE c.campaign_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Campaign c = mapRow(rs);
                c.setOrgName(rs.getString("org_name"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCampaign(Campaign c) {
        String sql = "INSERT INTO campaigns (org_id, name, description, target_amount, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getOrgId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getTargetAmount());
            ps.setDate(5, c.getStartDate());
            ps.setDate(6, c.getEndDate());
            ps.setString(7, c.getStatus() != null ? c.getStatus() : "Active");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM campaigns WHERE status = 'Active'";
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

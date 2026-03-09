package com.ngo.dao;

import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.*;

public class DashboardDAO {

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        String sql = "{CALL GetDashboardStats()}";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("total_users"));
                stats.put("totalDonors", rs.getInt("total_donors"));
                stats.put("totalVolunteers", rs.getInt("total_volunteers"));
                stats.put("totalDonations", rs.getInt("total_donations"));
                stats.put("totalCashRaised", rs.getDouble("total_cash_raised"));
                stats.put("activeCampaigns", rs.getInt("active_campaigns"));
                stats.put("pendingRequirements", rs.getInt("pending_requirements"));
                stats.put("totalDistributions", rs.getInt("total_distributions"));
                stats.put("lowStockItems", rs.getInt("low_stock_items"));
                stats.put("totalOrganizations", rs.getInt("total_organizations"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Map<String, Object>> getMonthlyDonationTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        String sql = "SELECT * FROM v_monthly_donations LIMIT 6";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("month", rs.getString("month"));
                row.put("donationCount", rs.getInt("donation_count"));
                row.put("cashTotal", rs.getDouble("cash_total"));
                row.put("goodsTotal", rs.getDouble("goods_total"));
                trends.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trends;
    }

    public List<Map<String, Object>> getCampaignProgress() {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM v_campaign_progress";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", rs.getString("name"));
                row.put("orgName", rs.getString("org_name"));
                row.put("targetAmount", rs.getDouble("target_amount"));
                row.put("raisedAmount", rs.getDouble("raised_amount"));
                row.put("progressPct", rs.getDouble("progress_pct"));
                row.put("status", rs.getString("status"));
                row.put("donationCount", rs.getInt("donation_count"));
                campaigns.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaigns;
    }

    public List<Map<String, Object>> getDonationsByType() {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT type, COUNT(*) AS count, SUM(amount_or_quantity) AS total FROM donations GROUP BY type";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("type", rs.getString("type"));
                row.put("count", rs.getInt("count"));
                row.put("total", rs.getDouble("total"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<Map<String, Object>> getUrgencyDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT urgency, COUNT(*) AS count FROM requirements WHERE status != 'Fulfilled' GROUP BY urgency";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("urgency", rs.getString("urgency"));
                row.put("count", rs.getInt("count"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}

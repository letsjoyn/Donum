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

    /** Last 6 calendar months including current — always reflects new donations. */
    public List<Map<String, Object>> getMonthlyDonationTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        String sql =
                "SELECT DATE_FORMAT(months.month_start, '%b %Y') AS month, " +
                "       COALESCE(cash.total, 0) AS cash_total, " +
                "       COALESCE(kind.total, 0) AS goods_total, " +
                "       COALESCE(cash.cnt, 0) + COALESCE(kind.cnt, 0) AS donation_count " +
                "FROM ( " +
                "  SELECT DATE_SUB(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL n MONTH) AS month_start " +
                "  FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) nums " +
                ") months " +
                "LEFT JOIN ( " +
                "  SELECT DATE_FORMAT(donation_date, '%Y-%m-01') AS md, " +
                "         SUM(amount_or_quantity) AS total, COUNT(*) AS cnt " +
                "  FROM donations WHERE type = 'Cash' GROUP BY md " +
                ") cash ON months.month_start = cash.md " +
                "LEFT JOIN ( " +
                "  SELECT DATE_FORMAT(donation_date, '%Y-%m-01') AS md, " +
                "         SUM(amount_or_quantity) AS total, COUNT(*) AS cnt " +
                "  FROM donations WHERE type = 'Kind' GROUP BY md " +
                ") kind ON months.month_start = kind.md " +
                "ORDER BY months.month_start ASC";
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
        String sql = "SELECT name, org_name, target_amount, raised_amount, progress_pct, status, donation_count " +
                     "FROM v_campaign_progress " +
                     "WHERE status IN ('Active', 'Planning') " +
                     "ORDER BY progress_pct DESC, raised_amount DESC LIMIT 10";
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
        String sql =
                "SELECT t.type, COALESCE(d.cnt, 0) AS count, COALESCE(d.total, 0) AS total " +
                "FROM (SELECT 'Cash' AS type UNION SELECT 'Kind') t " +
                "LEFT JOIN ( " +
                "  SELECT type, COUNT(*) AS cnt, SUM(amount_or_quantity) AS total " +
                "  FROM donations GROUP BY type " +
                ") d ON t.type = d.type";
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
        String sql =
                "SELECT u.urgency, COALESCE(r.cnt, 0) AS count " +
                "FROM ( " +
                "  SELECT 'Critical' AS urgency UNION SELECT 'High' " +
                "  UNION SELECT 'Medium' UNION SELECT 'Low' " +
                ") u " +
                "LEFT JOIN ( " +
                "  SELECT urgency, COUNT(*) AS cnt FROM requirements " +
                "  WHERE status != 'Fulfilled' GROUP BY urgency " +
                ") r ON u.urgency = r.urgency " +
                "ORDER BY FIELD(u.urgency, 'Critical', 'High', 'Medium', 'Low')";
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

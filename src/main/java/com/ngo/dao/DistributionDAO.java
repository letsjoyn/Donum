package com.ngo.dao;

import com.ngo.model.DistributionLog;
import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.*;

public class DistributionDAO {

    public boolean logDistribution(DistributionLog log) {
        String sql = "INSERT INTO distribution_log (donation_id, requirement_id, volunteer_id, quantity_distributed, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getDonationId() > 0) ps.setInt(1, log.getDonationId()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, log.getRequirementId());
            ps.setInt(3, log.getVolunteerId());
            ps.setDouble(4, log.getQuantityDistributed());
            ps.setString(5, log.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DistributionLog> getRecentDistributions(int limit) {
        List<DistributionLog> list = new ArrayList<>();
        String sql = "SELECT dl.*, u.full_name AS volunteer_name, r.location, r.item_name " +
                     "FROM distribution_log dl " +
                     "JOIN users u ON dl.volunteer_id = u.user_id " +
                     "JOIN requirements r ON dl.requirement_id = r.requirement_id " +
                     "ORDER BY dl.distributed_at DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DistributionLog dl = new DistributionLog();
                dl.setLogId(rs.getInt("log_id"));
                dl.setDonationId(rs.getInt("donation_id"));
                dl.setRequirementId(rs.getInt("requirement_id"));
                dl.setVolunteerId(rs.getInt("volunteer_id"));
                dl.setVolunteerName(rs.getString("volunteer_name"));
                dl.setLocation(rs.getString("location"));
                dl.setItemName(rs.getString("item_name"));
                dl.setQuantityDistributed(rs.getDouble("quantity_distributed"));
                dl.setNotes(rs.getString("notes"));
                dl.setDistributedAt(rs.getTimestamp("distributed_at"));
                list.add(dl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionLog> getDistributionsByVolunteer(int volunteerId) {
        List<DistributionLog> list = new ArrayList<>();
        String sql = "SELECT dl.*, r.location, r.item_name FROM distribution_log dl " +
                     "JOIN requirements r ON dl.requirement_id = r.requirement_id " +
                     "WHERE dl.volunteer_id = ? ORDER BY dl.distributed_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DistributionLog dl = new DistributionLog();
                dl.setLogId(rs.getInt("log_id"));
                dl.setRequirementId(rs.getInt("requirement_id"));
                dl.setLocation(rs.getString("location"));
                dl.setItemName(rs.getString("item_name"));
                dl.setQuantityDistributed(rs.getDouble("quantity_distributed"));
                dl.setNotes(rs.getString("notes"));
                dl.setDistributedAt(rs.getTimestamp("distributed_at"));
                list.add(dl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> getImpactReport(int donorId) {
        Map<String, Object> report = new HashMap<>();
        String sql = "{CALL GenerateImpactReport(?)}";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, donorId);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                report.put("fullName", rs.getString("full_name"));
                report.put("totalDonations", rs.getInt("total_donations"));
                report.put("totalCash", rs.getDouble("total_cash"));
                report.put("totalGoods", rs.getDouble("total_goods"));
                report.put("itemsDonated", rs.getString("items_donated"));
                report.put("successfulDistributions", rs.getInt("successful_distributions"));
                report.put("totalDelivered", rs.getDouble("total_quantity_delivered"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM distribution_log";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Deletes a distribution and reverses inventory / requirement updates from trg_after_distribution_insert.
     */
    public boolean deleteDistribution(int logId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double qty = 0;
                int requirementId = 0;
                String itemName = null;

                String fetchSql = "SELECT dl.quantity_distributed, dl.requirement_id, r.item_name " +
                        "FROM distribution_log dl JOIN requirements r ON dl.requirement_id = r.requirement_id " +
                        "WHERE dl.log_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                    ps.setInt(1, logId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    qty = rs.getDouble("quantity_distributed");
                    requirementId = rs.getInt("requirement_id");
                    itemName = rs.getString("item_name");
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE inventory SET quantity = quantity + ? WHERE item_name = ?")) {
                    ps.setDouble(1, qty);
                    ps.setString(2, itemName);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE requirements SET quantity_fulfilled = GREATEST(quantity_fulfilled - ?, 0), " +
                        "status = CASE " +
                        "  WHEN GREATEST(quantity_fulfilled - ?, 0) >= quantity_needed THEN 'Fulfilled' " +
                        "  WHEN GREATEST(quantity_fulfilled - ?, 0) > 0 THEN 'Partially Met' " +
                        "  ELSE 'Pending' END " +
                        "WHERE requirement_id = ?")) {
                    ps.setDouble(1, qty);
                    ps.setDouble(2, qty);
                    ps.setDouble(3, qty);
                    ps.setInt(4, requirementId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM distribution_log WHERE log_id = ?")) {
                    ps.setInt(1, logId);
                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

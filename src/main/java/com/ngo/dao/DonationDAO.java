package com.ngo.dao;

import com.ngo.model.Donation;
import com.ngo.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    private Donation mapRow(ResultSet rs) throws SQLException {
        Donation d = new Donation();
        d.setDonationId(rs.getInt("donation_id"));
        d.setDonorId(rs.getInt("donor_id"));
        d.setCampaignId(rs.getObject("campaign_id") != null ? rs.getInt("campaign_id") : null);
        d.setType(rs.getString("type"));
        d.setItemName(rs.getString("item_name"));
        d.setAmountOrQuantity(rs.getDouble("amount_or_quantity"));
        d.setStatus(rs.getString("status"));
        d.setNotes(rs.getString("notes"));
        d.setDonationDate(rs.getTimestamp("donation_date"));
        return d;
    }

    public boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donations (donor_id, campaign_id, type, item_name, amount_or_quantity, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, donation.getDonorId());
            if (donation.getCampaignId() != null) ps.setInt(2, donation.getCampaignId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, donation.getType());
            ps.setString(4, donation.getItemName());
            ps.setDouble(5, donation.getAmountOrQuantity());
            ps.setString(6, donation.getStatus() != null ? donation.getStatus() : "Received");
            ps.setString(7, donation.getNotes());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) donation.setDonationId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Donation> getDonationsByDonor(int donorId) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, c.name AS campaign_name FROM donations d " +
                     "LEFT JOIN campaigns c ON d.campaign_id = c.campaign_id " +
                     "WHERE d.donor_id = ? ORDER BY d.donation_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, donorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Donation d = mapRow(rs);
                d.setCampaignName(rs.getString("campaign_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Donation> getAllDonations() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS donor_name, c.name AS campaign_name FROM donations d " +
                     "JOIN users u ON d.donor_id = u.user_id " +
                     "LEFT JOIN campaigns c ON d.campaign_id = c.campaign_id " +
                     "ORDER BY d.donation_date DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Donation d = mapRow(rs);
                d.setDonorName(rs.getString("donor_name"));
                d.setCampaignName(rs.getString("campaign_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM donations";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalCashDonations() {
        String sql = "SELECT COALESCE(SUM(amount_or_quantity), 0) FROM donations WHERE type = 'Cash'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Donation> getRecentDonations(int limit) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name AS donor_name, c.name AS campaign_name FROM donations d " +
                     "JOIN users u ON d.donor_id = u.user_id " +
                     "LEFT JOIN campaigns c ON d.campaign_id = c.campaign_id " +
                     "ORDER BY d.donation_date DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Donation d = mapRow(rs);
                d.setDonorName(rs.getString("donor_name"));
                d.setCampaignName(rs.getString("campaign_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Donation findById(Connection conn, int donationId) throws SQLException {
        String sql = "SELECT * FROM donations WHERE donation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, donationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /**
     * Deletes a donation and reverses trigger side-effects (inventory / campaign totals).
     */
    public boolean deleteDonation(int donationId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Donation d = findById(conn, donationId);
                if (d == null) {
                    conn.rollback();
                    return false;
                }

                if ("Kind".equals(d.getType()) && d.getItemName() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inventory SET quantity = GREATEST(quantity - ?, 0) WHERE item_name = ?")) {
                        ps.setDouble(1, d.getAmountOrQuantity());
                        ps.setString(2, d.getItemName());
                        ps.executeUpdate();
                    }
                }

                if ("Cash".equals(d.getType()) && d.getCampaignId() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE campaigns SET raised_amount = GREATEST(raised_amount - ?, 0) WHERE campaign_id = ?")) {
                        ps.setDouble(1, d.getAmountOrQuantity());
                        ps.setInt(2, d.getCampaignId());
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM donations WHERE donation_id = ?")) {
                    ps.setInt(1, donationId);
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

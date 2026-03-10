package com.ngo.swing;

import com.ngo.dao.*;
import com.ngo.model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private static final Color BG = new Color(10, 10, 14);
    private static final Color SURFACE = new Color(18, 18, 22);
    private static final Color CARD = new Color(22, 22, 28);
    private static final Color BORDER = new Color(255, 255, 255, 12);
    private static final Color PRIMARY = new Color(0, 210, 255);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color DIM = new Color(120, 130, 150);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color ROW_ALT = new Color(16, 16, 20);
    private static final Font MONO = new Font("Consolas", Font.PLAIN, 12);
    private static final Font LABEL = new Font("SansSerif", Font.BOLD, 10);
    private static final DecimalFormat NUM_FMT = new DecimalFormat("#,##0");
    private static final DecimalFormat MONEY_FMT = new DecimalFormat("\u20B9#,##0");

    private User adminUser;
    private DefaultTableModel invModel, reqModel, donModel, campModel;
    private JTable invTable, reqTable, donTable, campTable;
    private TableRowSorter<DefaultTableModel> invSorter, reqSorter, donSorter, campSorter;
    private InventoryDAO inventoryDAO = new InventoryDAO();
    private RequirementDAO requirementDAO = new RequirementDAO();
    private DonationDAO donationDAO = new DonationDAO();
    private CampaignDAO campaignDAO = new CampaignDAO();
    private DashboardDAO dashboardDAO = new DashboardDAO();

    // Stat labels
    private JLabel statDonations, statAmount, statCampaigns, statLowStock, statPending;

    private CardLayout contentCards;
    private JPanel contentPanel;
    private int activeNav = 0;
    private JPanel[] navButtons;

    public AdminDashboard(User user) {
        this.adminUser = user;
        setTitle("Donum Admin \u2014 " + user.getFullName());
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        contentCards = new CardLayout();
        contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(BG);
        contentPanel.add(buildOverviewPanel(), "overview");
        contentPanel.add(buildTablePanel("INVENTORY", buildInventoryTable()), "inventory");
        contentPanel.add(buildTablePanel("REQUIREMENTS", buildRequirementsTable()), "requirements");
        contentPanel.add(buildTablePanel("DONATIONS", buildDonationsTable()), "donations");
        contentPanel.add(buildTablePanel("CAMPAIGNS", buildCampaignsTable()), "campaigns");
        add(contentPanel, BorderLayout.CENTER);

        loadAllData();
    }

    // ── Header ──
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel logo = new JLabel("\u2764  DONUM");
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setForeground(PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JLabel userName = new JLabel(adminUser.getFullName());
        userName.setFont(new Font("SansSerif", Font.BOLD, 12));
        userName.setForeground(TEXT);
        JLabel roleBadge = new JLabel(" ADMIN ");
        roleBadge.setFont(LABEL);
        roleBadge.setForeground(BG);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(PRIMARY);
        roleBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JButton logoutBtn = createFlatButton("LOGOUT", DANGER);
        logoutBtn.addActionListener(e -> { dispose(); new MainApp().setVisible(true); });

        right.add(userName);
        right.add(roleBadge);
        right.add(Box.createHorizontalStrut(8));
        right.add(logoutBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ──
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SURFACE);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(LABEL);
        navLabel.setForeground(DIM);
        navLabel.setBorder(BorderFactory.createEmptyBorder(16, 12, 8, 0));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);

        String[] labels = {"Overview", "Inventory", "Requirements", "Donations", "Campaigns"};
        String[] cards = {"overview", "inventory", "requirements", "donations", "campaigns"};
        navButtons = new JPanel[labels.length];

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JPanel navItem = new JPanel(new BorderLayout());
            navItem.setMaximumSize(new Dimension(180, 38));
            navItem.setBackground(i == 0 ? new Color(0, 210, 255, 15) : SURFACE);
            navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            navItem.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", i == 0 ? Font.BOLD : Font.PLAIN, 12));
            lbl.setForeground(i == 0 ? PRIMARY : DIM);
            navItem.add(lbl, BorderLayout.CENTER);

            if (i == 0) {
                JPanel accent = new JPanel();
                accent.setPreferredSize(new Dimension(3, 0));
                accent.setBackground(PRIMARY);
                navItem.add(accent, BorderLayout.WEST);
            }

            navItem.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    setActiveNav(idx);
                    contentCards.show(contentPanel, cards[idx]);
                }
                @Override public void mouseEntered(MouseEvent e) {
                    if (idx != activeNav) navItem.setBackground(new Color(255, 255, 255, 5));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (idx != activeNav) navItem.setBackground(SURFACE);
                }
            });

            navButtons[i] = navItem;
            sidebar.add(navItem);
        }

        sidebar.add(Box.createVerticalGlue());

        // Refresh button at bottom
        JButton refreshBtn = createFlatButton("REFRESH DATA", PRIMARY);
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.setMaximumSize(new Dimension(150, 32));
        refreshBtn.addActionListener(e -> loadAllData());
        sidebar.add(refreshBtn);
        sidebar.add(Box.createVerticalStrut(16));

        return sidebar;
    }

    private void setActiveNav(int idx) {
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setBackground(SURFACE);
            navButtons[i].setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            Component lbl = ((BorderLayout) navButtons[i].getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (lbl instanceof JLabel) {
                ((JLabel) lbl).setForeground(DIM);
                ((JLabel) lbl).setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            // Remove accent
            Component west = ((BorderLayout) navButtons[i].getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (west != null) navButtons[i].remove(west);
        }
        navButtons[idx].setBackground(new Color(0, 210, 255, 15));
        Component lbl = ((BorderLayout) navButtons[idx].getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (lbl instanceof JLabel) {
            ((JLabel) lbl).setForeground(PRIMARY);
            ((JLabel) lbl).setFont(new Font("SansSerif", Font.BOLD, 12));
        }
        JPanel accent = new JPanel();
        accent.setPreferredSize(new Dimension(3, 0));
        accent.setBackground(PRIMARY);
        navButtons[idx].add(accent, BorderLayout.WEST);
        navButtons[idx].revalidate();
        activeNav = idx;
    }

    // ── Overview Panel (stats) ──
    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("OVERVIEW");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(1, 5, 12, 0));
        statsGrid.setOpaque(false);

        statDonations = new JLabel("0");
        statAmount = new JLabel("\u20B90");
        statCampaigns = new JLabel("0");
        statLowStock = new JLabel("0");
        statPending = new JLabel("0");

        statsGrid.add(buildStatCard("TOTAL DONATIONS", statDonations, PRIMARY));
        statsGrid.add(buildStatCard("AMOUNT RAISED", statAmount, SUCCESS));
        statsGrid.add(buildStatCard("ACTIVE CAMPAIGNS", statCampaigns, new Color(139, 92, 246)));
        statsGrid.add(buildStatCard("LOW STOCK ITEMS", statLowStock, DANGER));
        statsGrid.add(buildStatCard("PENDING NEEDS", statPending, WARNING));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(statsGrid, BorderLayout.NORTH);

        // Recent activity tables below stats
        JPanel tables = new JPanel(new GridLayout(1, 2, 12, 0));
        tables.setOpaque(false);
        tables.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        // Low stock mini-table
        String[] lowCols = {"Item", "Qty", "Warehouse", "Status"};
        DefaultTableModel lowModel = new DefaultTableModel(lowCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable lowTable = createStyledTable(lowModel);
        tables.add(buildMiniTableCard("LOW STOCK ALERTS", lowTable));

        // Urgent requirements mini-table
        String[] urgCols = {"Item", "Location", "Needed", "Urgency"};
        DefaultTableModel urgModel = new DefaultTableModel(urgCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable urgTable = createStyledTable(urgModel);
        tables.add(buildMiniTableCard("URGENT REQUIREMENTS", urgTable));

        center.add(tables, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        // Store mini-table models for loading
        panel.putClientProperty("lowModel", lowModel);
        panel.putClientProperty("urgModel", urgModel);

        return panel;
    }

    private JPanel buildStatCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, accent),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL);
        lbl.setForeground(DIM);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(TEXT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        return card;
    }

    private JPanel buildMiniTableCard(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(LABEL);
        lbl.setForeground(DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(lbl, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(CARD);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ── Table Panels ──
    private JPanel buildTablePanel(String title, JComponent tableScroll) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(TEXT);

        // Search field
        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(220, 30));
        search.setFont(MONO);
        search.setBackground(CARD);
        search.setForeground(TEXT);
        search.setCaretColor(PRIMARY);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        // Placeholder text
        search.setText("Search...");
        search.setForeground(DIM);
        search.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (search.getText().equals("Search...")) {
                    search.setText("");
                    search.setForeground(TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (search.getText().isEmpty()) {
                    search.setText("Search...");
                    search.setForeground(DIM);
                }
            }
        });

        // Wire search to the right sorter
        TableRowSorter<DefaultTableModel> sorter = getSorterForTitle(title);
        if (sorter != null) {
            search.getDocument().addDocumentListener(new DocumentListener() {
                private void filter() {
                    String text = search.getText();
                    if (text.equals("Search...") || text.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                    }
                }
                @Override public void insertUpdate(DocumentEvent e) { filter(); }
                @Override public void removeUpdate(DocumentEvent e) { filter(); }
                @Override public void changedUpdate(DocumentEvent e) { filter(); }
            });
        }

        top.add(lbl, BorderLayout.WEST);
        top.add(search, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private TableRowSorter<DefaultTableModel> getSorterForTitle(String title) {
        switch (title) {
            case "INVENTORY": return invSorter;
            case "REQUIREMENTS": return reqSorter;
            case "DONATIONS": return donSorter;
            case "CAMPAIGNS": return campSorter;
            default: return null;
        }
    }

    // ── Inventory Table ──
    private JScrollPane buildInventoryTable() {
        String[] cols = {"ID", "Item", "Category", "Qty", "Unit", "Warehouse", "Status"};
        invModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        invTable = createStyledTable(invModel);
        invSorter = new TableRowSorter<>(invModel);
        invTable.setRowSorter(invSorter);
        invTable.getColumnModel().getColumn(0).setMaxWidth(50);
        invTable.getColumnModel().getColumn(4).setMaxWidth(60);
        return wrapTable(invTable);
    }

    // ── Requirements Table ──
    private JScrollPane buildRequirementsTable() {
        String[] cols = {"ID", "Item", "Location", "Needed", "Fulfilled", "Urgency", "Campaign"};
        reqModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reqTable = createStyledTable(reqModel);
        reqSorter = new TableRowSorter<>(reqModel);
        reqTable.setRowSorter(reqSorter);
        reqTable.getColumnModel().getColumn(0).setMaxWidth(50);
        return wrapTable(reqTable);
    }

    // ── Donations Table ──
    private JScrollPane buildDonationsTable() {
        String[] cols = {"ID", "Donor", "Type", "Item / Amount", "Campaign", "Status", "Date"};
        donModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        donTable = createStyledTable(donModel);
        donSorter = new TableRowSorter<>(donModel);
        donTable.setRowSorter(donSorter);
        donTable.getColumnModel().getColumn(0).setMaxWidth(50);
        return wrapTable(donTable);
    }

    // ── Campaigns Table ──
    private JScrollPane buildCampaignsTable() {
        String[] cols = {"ID", "Campaign", "Organization", "Target", "Raised", "Progress", "Status"};
        campModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        campTable = createStyledTable(campModel);
        campSorter = new TableRowSorter<>(campModel);
        campTable.setRowSorter(campSorter);
        campTable.getColumnModel().getColumn(0).setMaxWidth(50);
        return wrapTable(campTable);
    }

    // ── Styled Table ──
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0, 210, 255, 25));
        table.setSelectionForeground(TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(255, 255, 255, 8));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setFont(MONO);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(SURFACE);
        header.setForeground(DIM);
        header.setFont(LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Custom renderer for status coloring and alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                }
                c.setForeground(TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                String text = val != null ? val.toString() : "";
                String colName = t.getColumnName(col).toLowerCase();

                // Color code status/urgency columns
                if (colName.contains("status") || colName.contains("urgency")) {
                    String upper = text.toUpperCase();
                    if (upper.contains("CRITICAL") || upper.contains("OUT_OF_STOCK") || upper.contains("CANCELLED")) {
                        c.setForeground(DANGER);
                    } else if (upper.contains("ADEQUATE") || upper.contains("APPROVED") || upper.contains("ACTIVE") || upper.contains("COMPLETED")) {
                        c.setForeground(SUCCESS);
                    } else if (upper.contains("LOW") || upper.contains("PENDING") || upper.contains("HIGH")) {
                        c.setForeground(WARNING);
                    } else if (upper.contains("MEDIUM")) {
                        c.setForeground(PRIMARY);
                    }
                    setFont(new Font("SansSerif", Font.BOLD, 11));
                }

                // Color progress column
                if (colName.equals("progress")) {
                    c.setForeground(PRIMARY);
                    setFont(new Font("SansSerif", Font.BOLD, 11));
                }

                return c;
            }
        });

        return table;
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(CARD);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ── Buttons ──
    private JButton createFlatButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                } else {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(LABEL);
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }

    // ── Data Loading ──
    private void loadAllData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadStats();
                loadInventory();
                loadRequirements();
                loadDonations();
                loadCampaigns();
                loadOverviewTables();
                return null;
            }
        };
        worker.execute();
    }

    private void loadStats() {
        try {
            Map<String, Object> stats = dashboardDAO.getAdminStats();
            SwingUtilities.invokeLater(() -> {
                statDonations.setText(NUM_FMT.format(getInt(stats, "totalDonations")));
                statAmount.setText(MONEY_FMT.format(getDouble(stats, "totalAmount")));
                statCampaigns.setText(String.valueOf(getInt(stats, "activeCampaigns")));
                statLowStock.setText(String.valueOf(inventoryDAO.countLowStock()));
                statPending.setText(String.valueOf(requirementDAO.countPending()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInventory() {
        List<InventoryItem> items = inventoryDAO.getAllInventory();
        SwingUtilities.invokeLater(() -> {
            invModel.setRowCount(0);
            for (InventoryItem item : items) {
                invModel.addRow(new Object[]{
                    item.getItemId(), item.getItemName(), item.getCategory(),
                    NUM_FMT.format(item.getQuantity()), item.getUnit(),
                    item.getWarehouseName() != null ? item.getWarehouseName() : "\u2014",
                    item.getStockStatus()
                });
            }
        });
    }

    private void loadRequirements() {
        List<Requirement> reqs = requirementDAO.getAllRequirements();
        SwingUtilities.invokeLater(() -> {
            reqModel.setRowCount(0);
            for (Requirement r : reqs) {
                reqModel.addRow(new Object[]{
                    r.getRequirementId(), r.getItemName(), r.getLocation(),
                    NUM_FMT.format(r.getQuantityNeeded()),
                    NUM_FMT.format(r.getQuantityFulfilled()),
                    r.getUrgency(),
                    r.getCampaignName() != null ? r.getCampaignName() : "\u2014"
                });
            }
        });
    }

    private void loadDonations() {
        List<Donation> donations = donationDAO.getAllDonations();
        SwingUtilities.invokeLater(() -> {
            donModel.setRowCount(0);
            for (Donation d : donations) {
                String itemOrAmount = "Cash".equals(d.getType())
                        ? MONEY_FMT.format(d.getAmountOrQuantity())
                        : d.getItemName() + " (" + NUM_FMT.format(d.getAmountOrQuantity()) + ")";
                donModel.addRow(new Object[]{
                    d.getDonationId(), d.getDonorName(), d.getType(),
                    itemOrAmount,
                    d.getCampaignName() != null ? d.getCampaignName() : "\u2014",
                    d.getStatus(), d.getDonationDate()
                });
            }
        });
    }

    private void loadCampaigns() {
        List<Campaign> campaigns = campaignDAO.getAllCampaigns();
        SwingUtilities.invokeLater(() -> {
            campModel.setRowCount(0);
            for (Campaign c : campaigns) {
                String progress = String.format("%.0f%%", c.getProgressPercent());
                campModel.addRow(new Object[]{
                    c.getCampaignId(), c.getName(),
                    c.getOrgName() != null ? c.getOrgName() : "\u2014",
                    MONEY_FMT.format(c.getTargetAmount()),
                    MONEY_FMT.format(c.getRaisedAmount()),
                    progress, c.getStatus()
                });
            }
        });
    }

    private void loadOverviewTables() {
        // Low stock
        List<InventoryItem> lowStock = inventoryDAO.getLowStockItems();
        // Urgent requirements
        List<Requirement> pending = requirementDAO.getPendingRequirements();

        SwingUtilities.invokeLater(() -> {
            JPanel overview = (JPanel) contentPanel.getComponent(0);
            DefaultTableModel lowModel = (DefaultTableModel) overview.getClientProperty("lowModel");
            DefaultTableModel urgModel = (DefaultTableModel) overview.getClientProperty("urgModel");

            if (lowModel != null) {
                lowModel.setRowCount(0);
                for (InventoryItem item : lowStock) {
                    lowModel.addRow(new Object[]{
                        item.getItemName(), NUM_FMT.format(item.getQuantity()),
                        item.getWarehouseName() != null ? item.getWarehouseName() : "\u2014",
                        item.getStockStatus()
                    });
                }
            }

            if (urgModel != null) {
                urgModel.setRowCount(0);
                for (Requirement r : pending) {
                    urgModel.addRow(new Object[]{
                        r.getItemName(), r.getLocation(),
                        NUM_FMT.format(r.getQuantityNeeded()), r.getUrgency()
                    });
                }
            }
        });
    }

    private int getInt(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        return 0;
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).doubleValue();
        return 0.0;
    }
}

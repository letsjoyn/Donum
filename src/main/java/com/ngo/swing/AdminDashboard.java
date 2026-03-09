package com.ngo.swing;

import com.ngo.dao.InventoryDAO;
import com.ngo.dao.RequirementDAO;
import com.ngo.model.InventoryItem;
import com.ngo.model.Requirement;
import com.ngo.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private static final Color BG = new Color(15, 23, 42);
    private static final Color CARD = new Color(30, 41, 59);
    private static final Color PRIMARY = new Color(0, 210, 255);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color DIM = new Color(148, 163, 184);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color SUCCESS = new Color(16, 185, 129);

    private User adminUser;
    private DefaultTableModel invModel;
    private DefaultTableModel reqModel;
    private InventoryDAO inventoryDAO;
    private RequirementDAO requirementDAO;

    public AdminDashboard(User user) {
        this.adminUser = user;
        this.inventoryDAO = new InventoryDAO();
        this.requirementDAO = new RequirementDAO();

        setTitle("Donum Admin - " + user.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel("Donum - Inventory & Requirements");
        titleLabel.setForeground(PRIMARY);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel userLabel = new JLabel(user.getFullName() + " (Admin)");
        userLabel.setForeground(DIM);
        header.add(titleLabel, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Inventory Tab
        String[] invCols = {"ID", "Item", "Category", "Qty", "Unit", "Warehouse", "Status", "Updated"};
        invModel = new DefaultTableModel(invCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable invTable = createStyledTable(invModel);
        tabs.addTab("Inventory", new JScrollPane(invTable));

        // Requirements Tab
        String[] reqCols = {"ID", "Item", "Location", "Needed", "Fulfilled", "Urgency", "Campaign"};
        reqModel = new DefaultTableModel(reqCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reqTable = createStyledTable(reqModel);
        tabs.addTab("Requirements", new JScrollPane(reqTable));

        add(tabs, BorderLayout.CENTER);

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        footer.setBackground(CARD);

        JButton refreshBtn = createButton("Refresh", PRIMARY);
        refreshBtn.addActionListener(e -> loadData());

        JButton logoutBtn = createButton("Logout", DANGER);
        logoutBtn.addActionListener(e -> { dispose(); new MainApp().setVisible(true); });

        footer.add(refreshBtn);
        footer.add(logoutBtn);
        add(footer, BorderLayout.SOUTH);

        loadData();
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setSelectionBackground(PRIMARY.darker());
        table.setGridColor(new Color(255, 255, 255, 20));
        table.getTableHeader().setBackground(BG);
        table.getTableHeader().setForeground(DIM);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 32));
        return btn;
    }

    private void loadData() {
        invModel.setRowCount(0);
        List<InventoryItem> items = inventoryDAO.getAllInventory();
        for (InventoryItem item : items) {
            invModel.addRow(new Object[]{
                    item.getItemId(), item.getItemName(), item.getCategory(),
                    item.getQuantity(), item.getUnit(),
                    item.getWarehouseName() != null ? item.getWarehouseName() : "-",
                    item.getStockStatus(), item.getLastUpdated()
            });
        }

        reqModel.setRowCount(0);
        List<Requirement> reqs = requirementDAO.getPendingRequirements();
        for (Requirement r : reqs) {
            reqModel.addRow(new Object[]{
                    r.getRequirementId(), r.getItemName(), r.getLocation(),
                    r.getQuantityNeeded(), r.getQuantityFulfilled(), r.getUrgency(),
                    r.getCampaignName() != null ? r.getCampaignName() : "-"
            });
        }
    }
}

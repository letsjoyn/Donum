package com.ngo.swing;

import com.ngo.dao.UserDAO;
import com.ngo.model.User;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class MainApp extends JFrame {
    private static final Color BG = new Color(10, 10, 14);
    private static final Color CARD = new Color(22, 22, 28);
    private static final Color BORDER = new Color(255, 255, 255, 12);
    private static final Color PRIMARY = new Color(0, 210, 255);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color DIM = new Color(120, 130, 150);
    private static final Color FIELD_BG = new Color(16, 16, 20);
    private static final Color FIELD_BORDER = new Color(255, 255, 255, 18);
    private static final Color FIELD_FOCUS = new Color(0, 210, 255, 80);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private UserDAO userDAO;

    public MainApp() {
        userDAO = new UserDAO();
        setTitle("Donum — Admin Console");
        setSize(440, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(40, 36, 36, 36)));
        card.setPreferredSize(new Dimension(380, 420));

        // Icon
        JLabel icon = new JLabel("\u2764");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        icon.setForeground(PRIMARY);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("DONUM");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("ADMIN CONSOLE");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLabel = createFieldLabel("USERNAME");
        usernameField = new JTextField();
        styleField(usernameField);

        // Password
        JLabel passLabel = createFieldLabel("PASSWORD");
        passwordField = new JPasswordField();
        styleField(passwordField);

        // Login button
        JButton loginBtn = new JButton("SIGN IN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY.brighter());
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(10, 10, 14));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setForeground(BG);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setOpaque(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::handleLogin);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(239, 68, 68));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enter key binding
        passwordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin(null);
            }
        });

        card.add(icon);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        wrapper.add(card);
        add(wrapper);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(DIM);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_FOCUS),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            }
        });
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Enter both username and password.");
            return;
        }
        statusLabel.setText(" ");

        User user = userDAO.login(username, password);
        if (user != null) {
            if ("Admin".equals(user.getRole())) {
                dispose();
                new AdminDashboard(user).setVisible(true);
            } else {
                statusLabel.setText("Desktop console is admin-only.");
            }
        } else {
            statusLabel.setText("Invalid credentials.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

package com.ngo.swing;

import com.ngo.dao.UserDAO;
import com.ngo.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainApp extends JFrame {
    private static final Color BG = new Color(15, 23, 42);
    private static final Color CARD = new Color(30, 41, 59);
    private static final Color PRIMARY = new Color(0, 210, 255);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color DIM = new Color(148, 163, 184);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public MainApp() {
        userDAO = new UserDAO();

        setTitle("Donum Desktop - Login");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 20)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JLabel title = new JLabel("Donum");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Desktop Administration");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        styleField(usernameField, "Username");

        passwordField = new JPasswordField();
        styleField(passwordField, "Password");

        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setBackground(PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::handleLogin);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        wrapper.add(card);
        add(wrapper);
    }

    private void styleField(JTextField field, String placeholder) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(new Color(255, 255, 255, 10));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 20)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setToolTipText(placeholder);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            if ("Admin".equals(user.getRole())) {
                this.dispose();
                new AdminDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Welcome " + user.getFullName() + "! Desktop app is for Admin users only.",
                        "Access Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

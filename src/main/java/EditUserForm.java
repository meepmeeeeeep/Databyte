import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

public class EditUserForm extends JPanel {
    private String username;
    private UserManagement parent;

    // Add new constructor that takes username and parent component
    public EditUserForm(String username, UserManagement parent) {
        this.username = username;
        this.parent = parent;

        initComponents();

        // Add Left-Padding to Text Fields
        employeeNameField.setBorder(BorderFactory.createCompoundBorder(
                employeeNameField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                usernameField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                emailField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        contactNumberField.setBorder(BorderFactory.createCompoundBorder(
                contactNumberField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        roleField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });

        // Update the dashboard label
        dashboardLabel.setText("Editing User " + username);

        // Load user data
        loadUserData();
    }

    // Add method to load user data
    private void loadUserData() {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                employeeNameField.setText(rs.getString("employee_name"));
                usernameField.setText(rs.getString("username"));
                passwordField.setText(rs.getString("password"));
                emailField.setText(rs.getString("email"));
                contactNumberField.setText(rs.getString("contact_number"));
                roleField.setSelectedItem(rs.getString("role"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update the edit method
    private void edit(ActionEvent e) {
// Validate fields
        if (employeeNameField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                contactNumberField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate contact number format
        if (!isValidContactNumber(contactNumberField.getText().trim())) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid contact number format:\n" +
                            "- 11 digits (###########)\n" +
                            "- ####-###-####\n" +
                            "- #### ### ####\n" +
                            "- ###-####\n" +
                            "- ### ####\n" +
                            "- #######",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if new username already exists (excluding current user)
        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ? AND username != ?")) {

            checkStmt.setString(1, usernameField.getText().trim());
            checkStmt.setString(2, username); // Original username
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose a different username.",
                        "Duplicate Username",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update user in database
            String sql = "UPDATE users SET employee_name = ?, username = ?, password = ?, role = ?, email = ?, contact_number = ? WHERE username = ?";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, employeeNameField.getText().trim());
                pst.setString(2, usernameField.getText().trim());
                pst.setString(3, passwordField.getText().trim());
                pst.setString(4, roleField.getSelectedItem().toString());
                pst.setString(5, emailField.getText().trim());
                pst.setString(6, contactNumberField.getText().trim());
                pst.setString(7, username); // Original username for WHERE clause

                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                    parent.populateTable(); // Refresh the users table
                    // Close the edit form window
                    SwingUtilities.getWindowAncestor(this).dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update user. Please try again.",
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating user: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update the cancel method
    private void cancel(ActionEvent e) {
        // Close the edit form window
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        employeeNameLabel = new JTextField();
        employeeNameField = new JTextField();
        usernameLabel = new JTextField();
        usernameField = new JTextField();
        emailField = new JTextField();
        emailLabel = new JTextField();
        contactNumberField = new JTextField();
        contactNumberLabel = new JTextField();
        editButton = new JButton();
        cancelButton = new JButton();
        passwordLabel = new JTextField();
        passwordField = new JPasswordField();
        roleLabel = new JTextField();
        roleField = new JComboBox<>();

        //======== this ========
        setBackground(new Color(0xe8e7f4));
        setMinimumSize(new Dimension(850, 380));

        //======== sidePanel ========
        {
            sidePanel.setBackground(new Color(0x6c39c1));
            sidePanel.setMaximumSize(new Dimension(30, 32823));
            sidePanel.setMinimumSize(new Dimension(30, 62));
            sidePanel.setPreferredSize(new Dimension(30, 820));

            GroupLayout sidePanelLayout = new GroupLayout(sidePanel);
            sidePanel.setLayout(sidePanelLayout);
            sidePanelLayout.setHorizontalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGap(0, 30, Short.MAX_VALUE)
            );
            sidePanelLayout.setVerticalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGap(0, 450, Short.MAX_VALUE)
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Editing User");
            dashboardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dashboardLabel.setBackground(new Color(0xfcf8ff));
            dashboardLabel.setForeground(new Color(0x251779));
            dashboardLabel.setBorder(null);
            dashboardLabel.setFocusable(false);
            dashboardLabel.setEditable(false);

            GroupLayout windowTitleContainerLayout = new GroupLayout(windowTitleContainer);
            windowTitleContainer.setLayout(windowTitleContainerLayout);
            windowTitleContainerLayout.setHorizontalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(697, Short.MAX_VALUE))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0xfcf8ff));

            //---- employeeNameLabel ----
            employeeNameLabel.setText("Employee Name:");
            employeeNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            employeeNameLabel.setBackground(new Color(0xfcf8ff));
            employeeNameLabel.setForeground(new Color(0x897cce));
            employeeNameLabel.setBorder(null);
            employeeNameLabel.setFocusable(false);
            employeeNameLabel.setEditable(false);

            //---- employeeNameField ----
            employeeNameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            employeeNameField.setBackground(new Color(0xe8e7f4));

            //---- usernameLabel ----
            usernameLabel.setText("Username:");
            usernameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            usernameLabel.setBackground(new Color(0xfcf8ff));
            usernameLabel.setForeground(new Color(0x897cce));
            usernameLabel.setBorder(null);
            usernameLabel.setFocusable(false);
            usernameLabel.setEditable(false);

            //---- usernameField ----
            usernameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            usernameField.setBackground(new Color(0xe8e7f4));

            //---- emailField ----
            emailField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            emailField.setBackground(new Color(0xe8e7f4));

            //---- emailLabel ----
            emailLabel.setText("Email:");
            emailLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            emailLabel.setBackground(new Color(0xfcf8ff));
            emailLabel.setForeground(new Color(0x897cce));
            emailLabel.setBorder(null);
            emailLabel.setFocusable(false);
            emailLabel.setEditable(false);

            //---- contactNumberField ----
            contactNumberField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            contactNumberField.setBackground(new Color(0xe8e7f4));

            //---- contactNumberLabel ----
            contactNumberLabel.setText("Contact Number:");
            contactNumberLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            contactNumberLabel.setBackground(new Color(0xfcf8ff));
            contactNumberLabel.setForeground(new Color(0x897cce));
            contactNumberLabel.setBorder(null);
            contactNumberLabel.setFocusable(false);
            contactNumberLabel.setEditable(false);

            //---- editButton ----
            editButton.setText("EDIT");
            editButton.setBackground(new Color(0x6c39c1));
            editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            editButton.setForeground(new Color(0xfcf8ff));
            editButton.setFocusable(false);
            editButton.addActionListener(e -> edit(e));

            //---- cancelButton ----
            cancelButton.setText("CANCEL");
            cancelButton.setBackground(new Color(0x6c39c1));
            cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelButton.setForeground(new Color(0xfcf8ff));
            cancelButton.setFocusable(false);
            cancelButton.addActionListener(e -> cancel(e));

            //---- passwordLabel ----
            passwordLabel.setText("Password:");
            passwordLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            passwordLabel.setBackground(new Color(0xfcf8ff));
            passwordLabel.setForeground(new Color(0x897cce));
            passwordLabel.setBorder(null);
            passwordLabel.setFocusable(false);
            passwordLabel.setEditable(false);

            //---- passwordField ----
            passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            passwordField.setBackground(new Color(0xe8e7f4));

            //---- roleLabel ----
            roleLabel.setText("Role:");
            roleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            roleLabel.setBackground(new Color(0xfcf8ff));
            roleLabel.setForeground(new Color(0x897cce));
            roleLabel.setBorder(null);
            roleLabel.setFocusable(false);
            roleLabel.setEditable(false);

            //---- roleField ----
            roleField.setModel(new DefaultComboBoxModel<>(new String[] {
                "ADMIN",
                "MANAGER",
                "CASHIER",
                "STOCK CLERK"
            }));
            roleField.setFocusable(false);
            roleField.setBorder(null);
            roleField.setBackground(new Color(0xe8e7f4));
            roleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(employeeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeNameField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(contactNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(emailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(emailField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addComponent(contactNumberField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                            .addComponent(roleLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(roleField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(25, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(employeeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(employeeNameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(emailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emailField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(contactNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contactNumberField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(roleLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(roleField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(sidePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(windowTitleContainer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(25, 25, 25))
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JPanel panel1;
    private JTextField employeeNameLabel;
    private JTextField employeeNameField;
    private JTextField usernameLabel;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField emailLabel;
    private JTextField contactNumberField;
    private JTextField contactNumberLabel;
    private JButton editButton;
    private JButton cancelButton;
    private JTextField passwordLabel;
    private JPasswordField passwordField;
    private JTextField roleLabel;
    private JComboBox<String> roleField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    // Add these validation methods at class level in both forms
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidContactNumber(String contactNumber) {
        String contactRegex = "^(\\d{11}|\\d{4}-\\d{3}-\\d{4}|\\d{4} \\d{3} \\d{4}|\\d{3}-\\d{4}|\\d{3} \\d{4}|\\d{7})$";
        return contactNumber.matches(contactRegex);
    }
}

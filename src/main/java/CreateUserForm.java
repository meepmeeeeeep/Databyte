import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

public class CreateUserForm extends JPanel {
    public CreateUserForm() {
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
    }

    private void add(ActionEvent e) {
        // Get values from form fields
        String employeeName = employeeNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String role = (String) roleField.getSelectedItem();

        // Validate input fields
        if (employeeName.isEmpty() || username.isEmpty() || password.isEmpty() ||
                email.isEmpty() || contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // SQL query to insert new user
        String sql = "INSERT INTO users (username, password, role, email, contact_number) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);  // Consider hashing the password in production
            pstmt.setString(3, role);
            pstmt.setString(4, email);
            pstmt.setString(5, contactNumber);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this,
                        "User created successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Close the create user form
                SwingUtilities.getWindowAncestor(this).dispose();

                // Refresh the users table in UserManagement
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof JFrame) {
                        Component[] components = ((JFrame) window).getContentPane().getComponents();
                        for (Component component : components) {
                            if (component instanceof UserManagement) {
                                ((UserManagement) component).populateTable();
                                break;
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create user",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating user: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancel(ActionEvent e) {
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
        createButton = new JButton();
        cancelButton = new JButton();
        passwordLabel = new JTextField();
        passwordField = new JTextField();
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
            dashboardLabel.setText("Create User");
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
                        .addContainerGap(702, Short.MAX_VALUE))
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

            //---- createButton ----
            createButton.setText("CREATE");
            createButton.setBackground(new Color(0x6c39c1));
            createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            createButton.setForeground(new Color(0xfcf8ff));
            createButton.setFocusable(false);
            createButton.addActionListener(e -> add(e));

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
                                    .addComponent(createButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(createButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
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
    private JButton createButton;
    private JButton cancelButton;
    private JTextField passwordLabel;
    private JTextField passwordField;
    private JTextField roleLabel;
    private JComboBox<String> roleField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

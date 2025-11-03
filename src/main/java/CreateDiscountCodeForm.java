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

public class CreateDiscountCodeForm extends JPanel {
    private final DiscountCodes parentComponent;
    public CreateDiscountCodeForm(DiscountCodes parent) {
        this.parentComponent = parent;

        initComponents();

        // Add Left-Padding to Text Fields
        discountCodeField.setBorder(BorderFactory.createCompoundBorder(
                discountCodeField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        validFromField.setBorder(BorderFactory.createCompoundBorder(
                validFromField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        validUntilField.setBorder(BorderFactory.createCompoundBorder(
                validUntilField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        discountPercentageField.setBorder(BorderFactory.createCompoundBorder(
                discountPercentageField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        maxUsesField.setBorder(BorderFactory.createCompoundBorder(
                maxUsesField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        minimumPurchaseField.setBorder(BorderFactory.createCompoundBorder(
                minimumPurchaseField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
    }

    private void add(ActionEvent e) {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Get values from fields
        String code = discountCodeField.getText().trim().toUpperCase();
        double discountPercentage = Double.parseDouble(discountPercentageField.getText().trim());
        String validFrom = validFromField.getText().trim();
        String validUntil = validUntilField.getText().trim();
        int maxUses = Integer.parseInt(maxUsesField.getText().trim());
        double minimumPurchase = Double.parseDouble(minimumPurchaseField.getText().trim());

        // SQL query to insert new discount code
        String sql = "INSERT INTO discount_codes (code, discount_percentage, valid_from, valid_until, " +
                "max_uses, current_uses, is_active, minimum_purchase) " +
                "VALUES (?, ?, ?, ?, ?, 0, true, ?)";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, code);
            pst.setDouble(2, discountPercentage);
            pst.setDate(3, java.sql.Date.valueOf(validFrom));
            pst.setDate(4, java.sql.Date.valueOf(validUntil));
            pst.setInt(5, maxUses);
            pst.setDouble(6, minimumPurchase);

            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Discount code created successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                parentComponent.populateTable();
                SwingUtilities.getWindowAncestor(this).dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error creating discount code: " + ex.getMessage(),
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
        discountCodeLabel = new JTextField();
        discountCodeField = new JTextField();
        validFromLabel = new JTextField();
        validFromField = new JTextField();
        discountPercentageField = new JTextField();
        discountPercentageLabel = new JTextField();
        maxUsesField = new JTextField();
        maxUsesLabel = new JTextField();
        createButton = new JButton();
        cancelButton = new JButton();
        validUntilLabel = new JTextField();
        minimumPurchaseLabel = new JTextField();
        minimumPurchaseField = new JTextField();
        validUntilField = new JTextField();

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
            dashboardLabel.setText("Create Discount Code");
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
                        .addContainerGap(620, Short.MAX_VALUE))
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

            //---- discountCodeLabel ----
            discountCodeLabel.setText("Discount Code:");
            discountCodeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            discountCodeLabel.setBackground(new Color(0xfcf8ff));
            discountCodeLabel.setForeground(new Color(0x897cce));
            discountCodeLabel.setBorder(null);
            discountCodeLabel.setFocusable(false);
            discountCodeLabel.setEditable(false);

            //---- discountCodeField ----
            discountCodeField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            discountCodeField.setBackground(new Color(0xe8e7f4));

            //---- validFromLabel ----
            validFromLabel.setText("Valid From:");
            validFromLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            validFromLabel.setBackground(new Color(0xfcf8ff));
            validFromLabel.setForeground(new Color(0x897cce));
            validFromLabel.setBorder(null);
            validFromLabel.setFocusable(false);
            validFromLabel.setEditable(false);

            //---- validFromField ----
            validFromField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            validFromField.setBackground(new Color(0xe8e7f4));

            //---- discountPercentageField ----
            discountPercentageField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            discountPercentageField.setBackground(new Color(0xe8e7f4));

            //---- discountPercentageLabel ----
            discountPercentageLabel.setText("Discount Percentage:");
            discountPercentageLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            discountPercentageLabel.setBackground(new Color(0xfcf8ff));
            discountPercentageLabel.setForeground(new Color(0x897cce));
            discountPercentageLabel.setBorder(null);
            discountPercentageLabel.setFocusable(false);
            discountPercentageLabel.setEditable(false);

            //---- maxUsesField ----
            maxUsesField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            maxUsesField.setBackground(new Color(0xe8e7f4));

            //---- maxUsesLabel ----
            maxUsesLabel.setText("Max Uses:");
            maxUsesLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            maxUsesLabel.setBackground(new Color(0xfcf8ff));
            maxUsesLabel.setForeground(new Color(0x897cce));
            maxUsesLabel.setBorder(null);
            maxUsesLabel.setFocusable(false);
            maxUsesLabel.setEditable(false);

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

            //---- validUntilLabel ----
            validUntilLabel.setText("Valid Until:");
            validUntilLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            validUntilLabel.setBackground(new Color(0xfcf8ff));
            validUntilLabel.setForeground(new Color(0x897cce));
            validUntilLabel.setBorder(null);
            validUntilLabel.setFocusable(false);
            validUntilLabel.setEditable(false);

            //---- minimumPurchaseLabel ----
            minimumPurchaseLabel.setText("Minimum Purchase:");
            minimumPurchaseLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            minimumPurchaseLabel.setBackground(new Color(0xfcf8ff));
            minimumPurchaseLabel.setForeground(new Color(0x897cce));
            minimumPurchaseLabel.setBorder(null);
            minimumPurchaseLabel.setFocusable(false);
            minimumPurchaseLabel.setEditable(false);

            //---- minimumPurchaseField ----
            minimumPurchaseField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            minimumPurchaseField.setBackground(new Color(0xe8e7f4));

            //---- validUntilField ----
            validUntilField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            validUntilField.setBackground(new Color(0xe8e7f4));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(discountCodeField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(validFromLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(validFromField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(validUntilLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(validUntilField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(maxUsesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(discountPercentageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(discountPercentageField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addComponent(maxUsesField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(createButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                            .addComponent(minimumPurchaseLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(minimumPurchaseField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(25, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(discountCodeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(discountPercentageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(discountPercentageField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(validFromLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(validFromField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(maxUsesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxUsesField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(validUntilLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(minimumPurchaseLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(minimumPurchaseField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(validUntilField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
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
    private JTextField discountCodeLabel;
    private JTextField discountCodeField;
    private JTextField validFromLabel;
    private JTextField validFromField;
    private JTextField discountPercentageField;
    private JTextField discountPercentageLabel;
    private JTextField maxUsesField;
    private JTextField maxUsesLabel;
    private JButton createButton;
    private JButton cancelButton;
    private JTextField validUntilLabel;
    private JTextField minimumPurchaseLabel;
    private JTextField minimumPurchaseField;
    private JTextField validUntilField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private boolean validateInputs() {
        // Validate code
        if (discountCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a discount code.");
            return false;
        }

        // Validate discount percentage
        try {
            double discount = Double.parseDouble(discountPercentageField.getText().trim());
            if (discount <= 0 || discount > 100) {
                JOptionPane.showMessageDialog(this, "Discount percentage must be between 0 and 100.");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid discount percentage.");
            return false;
        }

        // Validate dates
        try {
            java.sql.Date validFrom = java.sql.Date.valueOf(validFromField.getText().trim());
            java.sql.Date validUntil = java.sql.Date.valueOf(validUntilField.getText().trim());
            if (validUntil.before(validFrom)) {
                JOptionPane.showMessageDialog(this, "Valid until date must be after valid from date.");
                return false;
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates in YYYY-MM-DD format.");
            return false;
        }

        // Validate max uses
        try {
            int maxUses = Integer.parseInt(maxUsesField.getText().trim());
            if (maxUses <= 0) {
                JOptionPane.showMessageDialog(this, "Maximum uses must be greater than 0.");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for maximum uses.");
            return false;
        }

        // Validate minimum purchase
        try {
            double minPurchase = Double.parseDouble(minimumPurchaseField.getText().trim());
            if (minPurchase < 0) {
                JOptionPane.showMessageDialog(this, "Minimum purchase cannot be negative.");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount for minimum purchase.");
            return false;
        }

        return true;
    }
}

// PaymentConfirmationForm.java

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class PaymentConfirmationForm extends JPanel {
    private final Sales sales;
    private final String transactionId;
    private final double totalAmount;
    private final String customerName;
    private final String customerAddress;
    private final String customerEmail;
    private final String customerPhone;
    private double originalAmount = 0.0;
    private boolean discountApplied = false;
    private DefaultTableModel cartTableModel;
    private Map<String, Integer> itemQuantityMap;
    private double discountedTotal;
    private double paymentWithServiceFee;

    public PaymentConfirmationForm(Sales sales, String transactionId, Object[][] cartData, double totalAmount,
                                   String customerName, String customerAddress, String customerEmail, String customerPhone) {
        this.sales = sales;
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;

        initComponents();
        setupCartTable(cartData);

        // Add Left-Padding to Text Fields
        //---- Customer Information ----
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
                customerNameField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerAddressField.setBorder(BorderFactory.createCompoundBorder(
                customerAddressField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerEmailField.setBorder(BorderFactory.createCompoundBorder(
                customerEmailField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerPhoneField.setBorder(BorderFactory.createCompoundBorder(
                customerPhoneField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        //---- Payment Details ----
        totalAmountField.setBorder(BorderFactory.createCompoundBorder(
                totalAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentAmountField.setBorder(BorderFactory.createCompoundBorder(
                paymentAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        discountCodeField.setBorder(BorderFactory.createCompoundBorder(
                discountCodeField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentMethodField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });

        // Set Text Fields Values
        // ---- Customer Information ----
        customerNameField.setText(customerName);
        customerAddressField.setText(customerAddress);
        customerEmailField.setText(customerEmail);
        customerPhoneField.setText(customerPhone);
        // ---- Payment Details ----
        totalAmountField.setText(String.format("%.2f", totalAmount));

        // Make table rows non-selectable
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowSelectionAllowed(false);
        cartTable.setCellSelectionEnabled(false);
        cartTable.getTableHeader().setReorderingAllowed(false);
        cartTable.setFocusable(false);
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Confirmation Form
    }

    private void cancelDiscountButton(ActionEvent e) {
        if (!discountApplied) {
            JOptionPane.showMessageDialog(this,
                    "No discount is currently applied.",
                    "No Discount",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            // Get the current discount code from the field
            String code = discountCodeField.getText().trim().toUpperCase();

            // Update the discount code usage in the database (decrement)
            String updateSql = "UPDATE discount_codes SET current_uses = current_uses - 1 WHERE code = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, code);
            updateStmt.executeUpdate();

            // Reset the UI
            totalAmountField.setText(String.format("%.2f", originalAmount));

            if (paymentMethodField.getSelectedItem().equals("Card")) {

                paymentWithServiceFee = totalAmount + 15 + (totalAmount * 0.035);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

            } else if (paymentMethodField.getSelectedItem().equals("GCash")) {

                paymentWithServiceFee = totalAmount + 15 + (totalAmount * 0.030);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

            }

            discountApplied = false;
            discountCodeField.setEnabled(true);
            discountCodeField.setText("");
            applyDiscountButton.setEnabled(true);

            JOptionPane.showMessageDialog(this,
                    "Discount removed. Original price restored.",
                    "Discount Removed",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirm(ActionEvent e) {
        try {
            // Get the values and remove any currency symbols, spaces, and commas
            String totalStr = totalAmountField.getText().trim().replaceAll("[^\\d.]", "");

            if (paymentMethodField.getSelectedItem().equals("Card") ||
                    paymentMethodField.getSelectedItem().equals("GCash")) {
                totalStr = String.format("%.2f", paymentWithServiceFee).replaceAll("[^\\d.]", "");

            }

            String paymentStr = paymentAmountField.getText().trim().replaceAll("[^\\d.]", "");
            String paymentMethod = (String) paymentMethodField.getSelectedItem();

            if (totalStr.isEmpty() || paymentStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Payment amount and total must not be empty.");
                return;
            }

            double total = Double.parseDouble(totalStr);
            double payment = Double.parseDouble(paymentStr);

            if (payment < total) {
                JOptionPane.showMessageDialog(null, "Insufficient payment amount.");
                return;
            }

            double change = payment - total;

            DBConnection db = new DBConnection();
            String transactionID = db.generateTransactionID();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                // Begin transaction
                conn.setAutoCommit(false);

                try {
                    // Insert transaction record
                    String sql = "INSERT INTO transaction_history (transaction_id, total_price, date, customer_name, " +
                            "customer_address, customer_email, customer_phone, payment_amount, payment_method, discount_code, employee_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, transactionID);
                    stmt.setDouble(2, total);
                    stmt.setString(3, date);
                    stmt.setString(4, customerNameField.getText().trim());
                    stmt.setString(5, customerAddressField.getText().trim());
                    stmt.setString(6, customerEmailField.getText().trim());
                    stmt.setString(7, customerPhoneField.getText().trim());
                    stmt.setDouble(8, payment);
                    stmt.setString(9, paymentMethod);
                    stmt.setString(10, discountApplied ? discountCodeField.getText().trim() : null);
                    stmt.setString(11, UserSession.getEmployeeName());

                    stmt.executeUpdate();

                    // Insert cart items
                    String cartSql = "INSERT INTO cart_items (transaction_id, item_id, item_name, category, " +
                            "price, vat_type, vat_exclusive_price, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement cartStmt = conn.prepareStatement(cartSql);

                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        cartStmt.setString(1, transactionID);
                        cartStmt.setString(2, cartTableModel.getValueAt(i, 0).toString()); // item_id
                        cartStmt.setString(3, cartTableModel.getValueAt(i, 1).toString()); // item_name
                        cartStmt.setString(4, cartTableModel.getValueAt(i, 2).toString()); // category
                        cartStmt.setDouble(5, Double.parseDouble(cartTableModel.getValueAt(i, 3).toString())); // price
                        cartStmt.setString(6, cartTableModel.getValueAt(i, 4).toString()); // vat_type
                        cartStmt.setDouble(7, Double.parseDouble(cartTableModel.getValueAt(i, 5).toString())); // vat_exclusive_price
                        cartStmt.setInt(8, Integer.parseInt(cartTableModel.getValueAt(i, 6).toString())); // quantity
                        cartStmt.executeUpdate();
                    }

                    // Update inventory
                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        String updateSql = "UPDATE inventory SET quantity = quantity - ? WHERE item_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, Integer.parseInt(cartTableModel.getValueAt(i, 6).toString()));
                        updateStmt.setString(2, cartTableModel.getValueAt(i, 0).toString());
                        updateStmt.executeUpdate();
                    }

                    // Commit transaction
                    conn.commit();

                    // Show payment details
                    PaymentDetails paymentDetails = new PaymentDetails(
                            String.format("%.2f", total),
                            String.format("%.2f", payment),
                            paymentMethod,
                            change,
                            sales
                    );

                    JFrame frame = new JFrame("Payment Details");
                    frame.setContentPane(paymentDetails);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setResizable(false);
                    frame.setVisible(true);

                    SwingUtilities.getWindowAncestor(this).dispose();

                } catch (SQLException ex) {
                    // Rollback transaction on error
                    conn.rollback();
                    throw ex;
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error saving transaction: " + ex.getMessage());
        }
    }

    private void applyDiscountButton(ActionEvent e) {
        if (discountApplied) {
            JOptionPane.showMessageDialog(this,
                    "A discount is already applied. Remove current discount first.",
                    "Discount Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = discountCodeField.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a discount code.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            // Updated SQL query to include minimum_purchase check
            String sql = "SELECT discount_percentage, current_uses, max_uses, minimum_purchase " +
                    "FROM discount_codes " +
                    "WHERE code = ? AND is_active = TRUE " +
                    "AND valid_from <= CURRENT_DATE " +
                    "AND valid_until >= CURRENT_DATE";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid or expired discount code.",
                        "Invalid Code",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double discountPercentage = rs.getDouble("discount_percentage");
            int currentUses = rs.getInt("current_uses");
            int maxUses = rs.getInt("max_uses");
            double minimumPurchase = rs.getDouble("minimum_purchase");

            // Check if current total meets minimum purchase requirement
            double currentTotal = Double.parseDouble(totalAmountField.getText().trim());
            if (currentTotal < minimumPurchase) {
                JOptionPane.showMessageDialog(this,
                        String.format("This discount code requires a minimum purchase of ₱%.2f",
                                minimumPurchase),
                        "Minimum Purchase Required",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentUses >= maxUses) {
                JOptionPane.showMessageDialog(this,
                        "This discount code has reached its maximum uses.",
                        "Code Expired",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {

                originalAmount = Double.parseDouble(totalAmountField.getText());
                double discountRate = discountPercentage / 100.0;
                double discountAmount = originalAmount * discountRate;
                discountedTotal = originalAmount - discountAmount;

                if (paymentMethodField.getSelectedItem().equals("Card")) {

                    paymentWithServiceFee = discountedTotal + 15 + (discountedTotal * 0.035);
                    paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

                } else if (paymentMethodField.getSelectedItem().equals("GCash")) {

                    paymentWithServiceFee = discountedTotal + 15 + (discountedTotal * 0.030);
                    paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

                }

                // Update the discount code usage in the database
                String updateSql = "UPDATE discount_codes SET current_uses = current_uses + 1 WHERE code = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, code);
                updateStmt.executeUpdate();

                totalAmountField.setText(String.format("%.2f", discountedTotal));
                discountApplied = true;
                discountCodeField.setEnabled(false);
                applyDiscountButton.setEnabled(false);

                JOptionPane.showMessageDialog(this,
                        String.format("Discount applied!\nOriginal amount: ₱%.2f\nDiscount amount: ₱%.2f (%.0f%%)\nDiscounted total: ₱%.2f",
                                originalAmount, discountAmount, discountPercentage, discountedTotal),
                        "Discount Applied",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error processing discount. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Change Payment Amount to same as Total Amount when Payment Method is Card or GCash
    private void paymentMethodFieldItemStateChanged(ItemEvent e) {
        // Service Fees:
        // 3.5% + 15 for Card
        // 3.0% + 15 for GCash
        if (paymentMethodField.getSelectedItem().equals("Card")) {
            if (discountApplied) {
                paymentWithServiceFee = discountedTotal + 15 + (discountedTotal * 0.035);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

            } else {
                paymentWithServiceFee = totalAmount + 15 + (totalAmount * 0.035);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));
                paymentAmountField.setEditable(false);
                paymentAmountField.setFocusable(false);
            }

        } else if (paymentMethodField.getSelectedItem().equals("GCash")) {
            if (discountApplied) {
                paymentWithServiceFee = discountedTotal + 15 + (discountedTotal * 0.030);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));

            } else {
                paymentWithServiceFee = totalAmount + 15 + (totalAmount * 0.030);
                paymentAmountField.setText(String.format("%.2f", paymentWithServiceFee));
                paymentAmountField.setEditable(false);
                paymentAmountField.setFocusable(false);
            }

        } else {
            paymentAmountField.setText("");
            paymentAmountField.setEditable(true);
            paymentAmountField.setFocusable(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        confirmButton = new JButton();
        cancelButton = new JButton();
        customerNameField = new JTextField();
        customeNameLabel = new JTextField();
        customerAddressLabel = new JTextField();
        customerAddressField = new JTextField();
        customerEmailLabel = new JTextField();
        customerEmailField = new JTextField();
        customerPhoneField = new JTextField();
        customerPhoneLabel = new JTextField();
        customerInformationLabel = new JTextField();
        paymentDetailsLabel = new JTextField();
        totalAmountLabel = new JTextField();
        totalAmountField = new JTextField();
        paymentAmountLabel = new JTextField();
        paymentAmountField = new JTextField();
        paymentMethodLabel = new JTextField();
        paymentMethodField = new JComboBox<>();
        panel2 = new JPanel();
        cartLabel = new JTextField();
        scrollPane1 = new JScrollPane();
        cartTable = new JTable();
        discountCodeLabel = new JTextField();
        discountCodeField = new JTextField();
        applyDiscountButton = new JButton();
        cancelDiscountButton = new JButton();

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
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Payment Confirmation");
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
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

            //---- confirmButton ----
            confirmButton.setText("CONFIRM");
            confirmButton.setBackground(new Color(0x6c39c1));
            confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            confirmButton.setForeground(new Color(0xfcf8ff));
            confirmButton.setFocusable(false);
            confirmButton.addActionListener(e -> confirm(e));

            //---- cancelButton ----
            cancelButton.setText("CANCEL");
            cancelButton.setBackground(new Color(0x6c39c1));
            cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelButton.setForeground(new Color(0xfcf8ff));
            cancelButton.setFocusable(false);
            cancelButton.addActionListener(e -> cancel(e));

            //---- customerNameField ----
            customerNameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerNameField.setBackground(new Color(0xe8e7f4));
            customerNameField.setEditable(false);
            customerNameField.setFocusable(false);
            customerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customeNameLabel ----
            customeNameLabel.setText("Customer Name:");
            customeNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customeNameLabel.setBackground(new Color(0xfcf8ff));
            customeNameLabel.setForeground(new Color(0x897cce));
            customeNameLabel.setBorder(null);
            customeNameLabel.setFocusable(false);
            customeNameLabel.setEditable(false);

            //---- customerAddressLabel ----
            customerAddressLabel.setText("Customer Address: (Optional)");
            customerAddressLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerAddressLabel.setBackground(new Color(0xfcf8ff));
            customerAddressLabel.setForeground(new Color(0x897cce));
            customerAddressLabel.setBorder(null);
            customerAddressLabel.setFocusable(false);
            customerAddressLabel.setEditable(false);

            //---- customerAddressField ----
            customerAddressField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerAddressField.setBackground(new Color(0xe8e7f4));
            customerAddressField.setEditable(false);
            customerAddressField.setFocusable(false);
            customerAddressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerEmailLabel ----
            customerEmailLabel.setText("Customer Email: (Optional)");
            customerEmailLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerEmailLabel.setBackground(new Color(0xfcf8ff));
            customerEmailLabel.setForeground(new Color(0x897cce));
            customerEmailLabel.setBorder(null);
            customerEmailLabel.setFocusable(false);
            customerEmailLabel.setEditable(false);

            //---- customerEmailField ----
            customerEmailField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerEmailField.setBackground(new Color(0xe8e7f4));
            customerEmailField.setEditable(false);
            customerEmailField.setFocusable(false);
            customerEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerPhoneField ----
            customerPhoneField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerPhoneField.setBackground(new Color(0xe8e7f4));
            customerPhoneField.setEditable(false);
            customerPhoneField.setFocusable(false);
            customerPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerPhoneLabel ----
            customerPhoneLabel.setText("Customer Phone Number: (Optional)");
            customerPhoneLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerPhoneLabel.setBackground(new Color(0xfcf8ff));
            customerPhoneLabel.setForeground(new Color(0x897cce));
            customerPhoneLabel.setBorder(null);
            customerPhoneLabel.setFocusable(false);
            customerPhoneLabel.setEditable(false);

            //---- customerInformationLabel ----
            customerInformationLabel.setText("Customer Information");
            customerInformationLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            customerInformationLabel.setBackground(new Color(0xfcf8ff));
            customerInformationLabel.setForeground(new Color(0x251779));
            customerInformationLabel.setBorder(null);
            customerInformationLabel.setFocusable(false);
            customerInformationLabel.setEditable(false);

            //---- paymentDetailsLabel ----
            paymentDetailsLabel.setText("Payment Details");
            paymentDetailsLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            paymentDetailsLabel.setBackground(new Color(0xfcf8ff));
            paymentDetailsLabel.setForeground(new Color(0x251779));
            paymentDetailsLabel.setBorder(null);
            paymentDetailsLabel.setFocusable(false);
            paymentDetailsLabel.setEditable(false);

            //---- totalAmountLabel ----
            totalAmountLabel.setText("Total Amount:");
            totalAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            totalAmountLabel.setBackground(new Color(0xfcf8ff));
            totalAmountLabel.setForeground(new Color(0x897cce));
            totalAmountLabel.setBorder(null);
            totalAmountLabel.setFocusable(false);
            totalAmountLabel.setEditable(false);

            //---- totalAmountField ----
            totalAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            totalAmountField.setEditable(false);
            totalAmountField.setFocusable(false);
            totalAmountField.setBackground(new Color(0xe8e7f4));
            totalAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- paymentAmountLabel ----
            paymentAmountLabel.setText("Payment Amount:");
            paymentAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            paymentAmountLabel.setBackground(new Color(0xfcf8ff));
            paymentAmountLabel.setForeground(new Color(0x897cce));
            paymentAmountLabel.setBorder(null);
            paymentAmountLabel.setFocusable(false);
            paymentAmountLabel.setEditable(false);

            //---- paymentAmountField ----
            paymentAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            paymentAmountField.setBackground(new Color(0xe8e7f4));
            paymentAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- paymentMethodLabel ----
            paymentMethodLabel.setText("Payment Method:");
            paymentMethodLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            paymentMethodLabel.setBackground(new Color(0xfcf8ff));
            paymentMethodLabel.setForeground(new Color(0x897cce));
            paymentMethodLabel.setBorder(null);
            paymentMethodLabel.setFocusable(false);
            paymentMethodLabel.setEditable(false);

            //---- paymentMethodField ----
            paymentMethodField.setModel(new DefaultComboBoxModel<>(new String[] {
                "Cash",
                "Card",
                "GCash"
            }));
            paymentMethodField.setFocusable(false);
            paymentMethodField.setBorder(null);
            paymentMethodField.setBackground(new Color(0xe8e7f4));
            paymentMethodField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            paymentMethodField.addItemListener(e -> paymentMethodFieldItemStateChanged(e));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                                        .addGap(50, 50, 50)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 20, Short.MAX_VALUE))))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 43, 43)
                        .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(22, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBackground(new Color(0xfcf8ff));

            //---- cartLabel ----
            cartLabel.setText("Cart");
            cartLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            cartLabel.setBackground(new Color(0xfcf8ff));
            cartLabel.setForeground(new Color(0x251779));
            cartLabel.setBorder(null);
            cartLabel.setFocusable(false);
            cartLabel.setEditable(false);

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(new Color(0xfcf8ff));
                scrollPane1.setViewportView(cartTable);
            }

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
            discountCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- applyDiscountButton ----
            applyDiscountButton.setText("APPLY");
            applyDiscountButton.setBackground(new Color(0x6c39c1));
            applyDiscountButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            applyDiscountButton.setForeground(new Color(0xfcf8ff));
            applyDiscountButton.setFocusable(false);
            applyDiscountButton.addActionListener(e -> applyDiscountButton(e));

            //---- cancelDiscountButton ----
            cancelDiscountButton.setText("REMOVE");
            cancelDiscountButton.setBackground(new Color(0x6c39c1));
            cancelDiscountButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelDiscountButton.setForeground(new Color(0xfcf8ff));
            cancelDiscountButton.setFocusable(false);
            cancelDiscountButton.addActionListener(e -> cancelDiscountButton(e));

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 394, Short.MAX_VALUE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(discountCodeField, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                                .addGap(20, 20, 20)
                                .addComponent(applyDiscountButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(cancelDiscountButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addGap(20, 20, 20)
                        .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelDiscountButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(applyDiscountButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(discountCodeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21))
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
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(25, 25, 25))
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JPanel panel1;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField customerNameField;
    private JTextField customeNameLabel;
    private JTextField customerAddressLabel;
    private JTextField customerAddressField;
    private JTextField customerEmailLabel;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JTextField customerPhoneLabel;
    private JTextField customerInformationLabel;
    private JTextField paymentDetailsLabel;
    private JTextField totalAmountLabel;
    private JTextField totalAmountField;
    private JTextField paymentAmountLabel;
    private JTextField paymentAmountField;
    private JTextField paymentMethodLabel;
    private JComboBox<String> paymentMethodField;
    private JPanel panel2;
    private JTextField cartLabel;
    private JScrollPane scrollPane1;
    private JTable cartTable;
    private JTextField discountCodeLabel;
    private JTextField discountCodeField;
    private JButton applyDiscountButton;
    private JButton cancelDiscountButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void setupCartTable(Object[][] cartData) {
        // Set up cart table model with columns
        String[] columns = {"Item ID", "Item Name", "Category", "Unit Price", "VAT Type", "VAT Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add the cart data
        for (Object[] row : cartData) {
            cartTableModel.addRow(row);
        }

        cartTable.setModel(cartTableModel);

        // Set table properties
        cartTable.setShowGrid(false);
        cartTable.setRowHeight(40);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.setFocusable(false);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);

        // Custom cell renderer for alternating row colors and padding
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    label.setBackground(Color.decode("#A59BDA"));
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.decode("#D4CFED") : Color.WHITE);
                    label.setForeground(Color.BLACK);
                }

                // Apply left padding (except first column)
                if (column != 0) {
                    label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }

                // Format numeric values
                if (value != null) {
                    if (column == 3 || column == 5 || column == 7) { // Price columns
                        label.setText(String.format("%.2f", Double.parseDouble(value.toString())));
                    }
                }

                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < cartTable.getColumnCount(); i++) {
            cartTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Style the header
        JTableHeader header = cartTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder());
                label.setBackground(Color.decode("#6c39c1"));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return label;
            }
        });

        // Set column widths
        TableColumnModel columnModel = cartTable.getColumnModel();
        int[] columnWidths = {100, 200, 150, 120, 100, 120, 100, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
            column.setMinWidth(100);
        }

        // Enable table scrolling
        cartTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Calculate total width
        int totalWidth = Arrays.stream(columnWidths).sum();

        // Set preferred size for the table
        cartTable.setPreferredScrollableViewportSize(new Dimension(totalWidth, 400));

        // Set scroll pane properties
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Make the table fill the viewport
        cartTable.setFillsViewportHeight(true);

        // Set preferred size for the scroll pane
        scrollPane1.setPreferredSize(new Dimension(505, 400));

        // Modern scroll bar styling
        scrollPane1.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());

        // Calculate and update total amount
        double total = 0.0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            Object subtotalObj = cartTableModel.getValueAt(i, 7);
            if (subtotalObj != null) {
                // Convert subtotal to string and remove any currency symbols or commas
                String subtotalStr = subtotalObj.toString().replaceAll("[^\\d.]", "");
                try {
                    double subtotal = Double.parseDouble(subtotalStr);
                    total += subtotal;
                } catch (NumberFormatException ex) {
                    System.err.println("Error parsing subtotal: " + subtotalObj);
                }
            }
        }
    }
}

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.table.*;

public class BankDetailsForm extends JPanel {
    private final Sales sales;
    private DefaultTableModel cartTableModel;
    private boolean discountApplied = false;
    private double paymentWithServiceFee;
    private String customerPhone;
    private String customerName;
    private String customerAddress;
    private String customerEmail;

    public BankDetailsForm(Sales sales, Object[][] cartData, double totalAmount,
                           String customerName, String customerAddress, String customerEmail, String customerPhone,
                           String paymentMethod, double payment, String discountCode, boolean discountApplied,
                           DefaultTableModel cartTableModel) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.sales = sales;
        this.cartTableModel = cartTableModel;
        this.discountApplied = discountApplied;
        this.paymentWithServiceFee = totalAmount;

        initComponents();

        // Add Left-Padding to Text Fields
        //---- Customer Information ----
        accountHolderField.setBorder(BorderFactory.createCompoundBorder(
                accountHolderField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        bankAccountField.setBorder(BorderFactory.createCompoundBorder(
                bankAccountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        cvvField.setBorder(BorderFactory.createCompoundBorder(
                cvvField.getBorder(),
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

        // Set field values
        totalAmountField.setText(String.format("%.2f", totalAmount));
        paymentAmountField.setText(String.format("%.2f", payment));
        paymentMethodField.setSelectedItem(paymentMethod);
        discountCodeField.setText(discountCode);

        // Make fields non-editable
        paymentMethodField.setFocusable(false);
        paymentMethodField.setEnabled(false);
        totalAmountField.setEditable(false);
        paymentAmountField.setEditable(false);
        discountCodeField.setEditable(false);

        // Disable CVV field if payment method is not Card
        if (!"Card".equals(paymentMethod)) {
            cvvField.setEditable(false);
            bankAccountLabel.setText("GCash Number: ");
            dashboardLabel.setText("GCash Information");
            customerInformationLabel.setText("GCash Information");
            cvvLabel.setVisible(false);
            cvvField.setVisible(false);
        }

        setupCartTable(cartData);
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
            String accountHolder = accountHolderField.getText().trim();
            String bankAccount = bankAccountField.getText().trim().replaceAll("\\s+", "");
            String cvv = cvvField.getText().trim();

            if (accountHolder.isEmpty() || bankAccount.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill in all bank account fields.");
                return;
            }

            if ("Card".equals(paymentMethod)) {
                // Validate card number (Luhn algorithm for VISA/Mastercard)
                if (!bankAccount.matches("\\d{13,19}") || !isValidCardNumber(bankAccount)) {
                    JOptionPane.showMessageDialog(null, "Invalid card number. Please enter a valid VISA/Mastercard number.");
                    return;
                }

                // Validate CVV (3 or 4 digits)
                if (!cvv.matches("\\d{3,4}")) {
                    JOptionPane.showMessageDialog(null, "Invalid CVV. Please enter a 3 or 4 digit CVV.");
                    return;
                }
            } else if ("GCash".equals(paymentMethod)) {
                // Validate GCash account number
                if (!bankAccount.matches("^(09|\\+639)\\d{9}$")) {
                    JOptionPane.showMessageDialog(null, "Invalid GCash account number. Please enter a valid mobile number.");
                    return;
                }
            }


            double total = Double.parseDouble(totalStr);
            double payment = Double.parseDouble(paymentStr);

            if (payment < total) {
                JOptionPane.showMessageDialog(null, "Insufficient payment amount.");
                return;
            }

            double change = payment - total;

            if (change > 1000) {
                JOptionPane.showMessageDialog(null, "Change is too large. Please enter a lower payment amount.");
                return;
            }

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
                    stmt.setString(4, customerName);
                    stmt.setString(5, customerAddress);
                    stmt.setString(6, customerEmail);
                    stmt.setString(7, customerPhone);
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

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Confirmation Form
    }

    private void paymentMethodFieldItemStateChanged(ItemEvent e) {
        // DISABLED - FIXED
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        confirmButton = new JButton();
        cancelButton = new JButton();
        accountHolderField = new JTextField();
        accountHolderLabel = new JTextField();
        bankAccountLabel = new JTextField();
        bankAccountField = new JTextField();
        cvvLabel = new JTextField();
        cvvField = new JTextField();
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
            dashboardLabel.setText("Bank Account Information");
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

            //---- accountHolderField ----
            accountHolderField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            accountHolderField.setBackground(new Color(0xe8e7f4));
            accountHolderField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- accountHolderLabel ----
            accountHolderLabel.setText("Account Holder Name:");
            accountHolderLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            accountHolderLabel.setBackground(new Color(0xfcf8ff));
            accountHolderLabel.setForeground(new Color(0x897cce));
            accountHolderLabel.setBorder(null);
            accountHolderLabel.setFocusable(false);
            accountHolderLabel.setEditable(false);

            //---- bankAccountLabel ----
            bankAccountLabel.setText("Bank Account Number:");
            bankAccountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            bankAccountLabel.setBackground(new Color(0xfcf8ff));
            bankAccountLabel.setForeground(new Color(0x897cce));
            bankAccountLabel.setBorder(null);
            bankAccountLabel.setFocusable(false);
            bankAccountLabel.setEditable(false);

            //---- bankAccountField ----
            bankAccountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            bankAccountField.setBackground(new Color(0xe8e7f4));
            bankAccountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- cvvLabel ----
            cvvLabel.setText("CVV:");
            cvvLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            cvvLabel.setBackground(new Color(0xfcf8ff));
            cvvLabel.setForeground(new Color(0x897cce));
            cvvLabel.setBorder(null);
            cvvLabel.setFocusable(false);
            cvvLabel.setEditable(false);

            //---- cvvField ----
            cvvField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            cvvField.setBackground(new Color(0xe8e7f4));
            cvvField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerInformationLabel ----
            customerInformationLabel.setText("Bank Account Information");
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
            totalAmountLabel.setForeground(new Color(0x251779));
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
            paymentAmountLabel.setForeground(new Color(0x251779));
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
            paymentMethodLabel.setForeground(new Color(0x251779));
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
                                    .addComponent(bankAccountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(accountHolderLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(accountHolderField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bankAccountField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE))
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
                                    .addComponent(cvvLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cvvField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 20, Short.MAX_VALUE))))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(accountHolderLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(accountHolderField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bankAccountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bankAccountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cvvLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cvvField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
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
                        .addContainerGap(87, Short.MAX_VALUE))
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
                            .addComponent(discountCodeField, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                        .addGap(20, 20, 20))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                        .addGap(20, 20, 20)
                        .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(discountCodeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
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
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
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
    private JTextField accountHolderField;
    private JTextField accountHolderLabel;
    private JTextField bankAccountLabel;
    private JTextField bankAccountField;
    private JTextField cvvLabel;
    private JTextField cvvField;
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

    private boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}

// PaymentConfirmationForm.java

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentConfirmationForm extends JPanel {
    private final Sales sales;
    public PaymentConfirmationForm(
            String customerName, String customerAddress, String customerEmail, String customerPhone,
            String itemID, String itemName, String category, double price, int quantity, double totalAmount,
            Sales sales) {
        this.sales = sales;
        initComponents();

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
        //---- Product Information ----
        itemNameField.setBorder(BorderFactory.createCompoundBorder(
                itemNameField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        itemIDField.setBorder(BorderFactory.createCompoundBorder(
                itemIDField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        priceField.setBorder(BorderFactory.createCompoundBorder(
                priceField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
                quantityField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        categoryField.setBorder(BorderFactory.createCompoundBorder(
                categoryField.getBorder(),
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

        // Set Text Fields Values
        // ---- Customer Information ----
        customerNameField.setText(customerName);
        customerAddressField.setText(customerAddress);
        customerEmailField.setText(customerEmail);
        customerPhoneField.setText(customerPhone);
        // ---- Product Information ----
        itemIDField.setText(itemID);
        itemNameField.setText(itemName);
        categoryField.setText(category);
        priceField.setText(String.valueOf(price));
        quantityField.setText(String.valueOf(quantity));
        // ---- Payment Details ----
        totalAmountField.setText(String.valueOf(totalAmount));
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Confirmation Form
    }

    private void confirm(ActionEvent e) {
        try {
            String totalStr = totalAmountField.getText().trim();
            String paymentStr = paymentAmountField.getText().trim();
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
                String sql = "INSERT INTO transaction_history (transaction_id, item_id, item_name, category, quantity, price, total_price, date, customer_name, customer_address, customer_email, customer_phone, payment_amount, payment_method) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, transactionID);
                stmt.setString(2, itemIDField.getText().trim());
                stmt.setString(3, itemNameField.getText().trim());
                stmt.setString(4, categoryField.getText().trim());
                stmt.setInt(5, Integer.parseInt(quantityField.getText()));
                stmt.setDouble(6, Double.parseDouble(priceField.getText()));
                stmt.setDouble(7, total);
                stmt.setString(8, date);
                stmt.setString(9, customerNameField.getText().trim());
                stmt.setString(10, customerAddressField.getText().trim());
                stmt.setString(11, customerEmailField.getText().trim());
                stmt.setString(12, customerPhoneField.getText().trim());
                stmt.setDouble(13, payment);
                stmt.setString(14, paymentMethod);

                stmt.executeUpdate();

                // Update inventory quantity
                String updateSql = "UPDATE inventory SET quantity = quantity - ? WHERE item_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, Integer.parseInt(quantityField.getText().trim()));
                updateStmt.setString(2, itemIDField.getText().trim());
                updateStmt.executeUpdate();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error saving transaction: " + ex.getMessage());
                return;
            }

            // Show payment details in new window
            String totalAmount = totalAmountField.getText();
            String paymentAmount = paymentAmountField.getText();
            String paymentChoice = (String) paymentMethodField.getSelectedItem();

            PaymentDetails paymentDetails = new PaymentDetails(totalAmount, paymentAmount, paymentChoice, change, sales);

            // Open PaymentConfirmationForm
            JFrame frame = new JFrame("Payment Details");
            frame.setContentPane(paymentDetails);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);          // Disable window resizing
            frame.setVisible(true);

            SwingUtilities.getWindowAncestor(this).dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values.");
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        itemNameLabel = new JTextField();
        itemNameField = new JTextField();
        itemIDLabel = new JTextField();
        itemIDField = new JTextField();
        priceField = new JTextField();
        priceLabel = new JTextField();
        quantityField = new JTextField();
        quantityLabel = new JTextField();
        confirmButton = new JButton();
        cancelButton = new JButton();
        categoryLabel = new JTextField();
        categoryField = new JTextField();
        customerNameField = new JTextField();
        customeNameLabel = new JTextField();
        customerAddressLabel = new JTextField();
        customerAddressField = new JTextField();
        customerEmailLabel = new JTextField();
        customerEmailField = new JTextField();
        customerPhoneField = new JTextField();
        customerPhoneLabel = new JTextField();
        customerInformationLabel = new JTextField();
        productInformationLabel = new JTextField();
        paymentDetailsLabel = new JTextField();
        totalAmountLabel = new JTextField();
        totalAmountField = new JTextField();
        paymentAmountLabel = new JTextField();
        paymentAmountField = new JTextField();
        paymentMethodLabel = new JTextField();
        paymentMethodField = new JComboBox<>();

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
                        .addContainerGap(607, Short.MAX_VALUE))
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

            //---- itemNameLabel ----
            itemNameLabel.setText("Item Name:");
            itemNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            itemNameLabel.setBackground(new Color(0xfcf8ff));
            itemNameLabel.setForeground(new Color(0x897cce));
            itemNameLabel.setBorder(null);
            itemNameLabel.setFocusable(false);
            itemNameLabel.setEditable(false);

            //---- itemNameField ----
            itemNameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            itemNameField.setBackground(new Color(0xe8e7f4));
            itemNameField.setEditable(false);
            itemNameField.setFocusable(false);
            itemNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- itemIDLabel ----
            itemIDLabel.setText("Item ID:");
            itemIDLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            itemIDLabel.setBackground(new Color(0xfcf8ff));
            itemIDLabel.setForeground(new Color(0x897cce));
            itemIDLabel.setBorder(null);
            itemIDLabel.setFocusable(false);
            itemIDLabel.setEditable(false);

            //---- itemIDField ----
            itemIDField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            itemIDField.setBackground(new Color(0xe8e7f4));
            itemIDField.setEditable(false);
            itemIDField.setFocusable(false);
            itemIDField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- priceField ----
            priceField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            priceField.setBackground(new Color(0xe8e7f4));
            priceField.setEditable(false);
            priceField.setFocusable(false);
            priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- priceLabel ----
            priceLabel.setText("Price:");
            priceLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            priceLabel.setBackground(new Color(0xfcf8ff));
            priceLabel.setForeground(new Color(0x897cce));
            priceLabel.setBorder(null);
            priceLabel.setFocusable(false);
            priceLabel.setEditable(false);

            //---- quantityField ----
            quantityField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            quantityField.setBackground(new Color(0xe8e7f4));
            quantityField.setEditable(false);
            quantityField.setFocusable(false);
            quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- quantityLabel ----
            quantityLabel.setText("Quantity:");
            quantityLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            quantityLabel.setBackground(new Color(0xfcf8ff));
            quantityLabel.setForeground(new Color(0x897cce));
            quantityLabel.setBorder(null);
            quantityLabel.setFocusable(false);
            quantityLabel.setEditable(false);

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

            //---- categoryLabel ----
            categoryLabel.setText("Category:");
            categoryLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            categoryLabel.setBackground(new Color(0xfcf8ff));
            categoryLabel.setForeground(new Color(0x897cce));
            categoryLabel.setBorder(null);
            categoryLabel.setFocusable(false);
            categoryLabel.setEditable(false);

            //---- categoryField ----
            categoryField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            categoryField.setBackground(new Color(0xe8e7f4));
            categoryField.setEditable(false);
            categoryField.setFocusable(false);
            categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

            //---- productInformationLabel ----
            productInformationLabel.setText("Product Information");
            productInformationLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            productInformationLabel.setBackground(new Color(0xfcf8ff));
            productInformationLabel.setForeground(new Color(0x251779));
            productInformationLabel.setBorder(null);
            productInformationLabel.setFocusable(false);
            productInformationLabel.setEditable(false);

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
            paymentMethodLabel.setForeground(new Color(0x251779));
            paymentMethodLabel.setBorder(null);
            paymentMethodLabel.setFocusable(false);
            paymentMethodLabel.setEditable(false);

            //---- paymentMethodField ----
            paymentMethodField.setModel(new DefaultComboBoxModel<>(new String[] {
                "Cash"
            }));
            paymentMethodField.setFocusable(false);
            paymentMethodField.setBorder(null);
            paymentMethodField.setBackground(new Color(0xe8e7f4));
            paymentMethodField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                                .addGap(50, 50, 50)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(119, Short.MAX_VALUE))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 30, Short.MAX_VALUE))))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(itemNameField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                                        .addGap(50, 50, 50)
                                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(priceField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                            .addComponent(quantityField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))))
                                .addGap(0, 0, Short.MAX_VALUE))))
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
                        .addGap(18, 18, 18)
                        .addComponent(productInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemNameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(priceField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(quantityField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
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
                        .addContainerGap(20, Short.MAX_VALUE))
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
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JPanel panel1;
    private JTextField itemNameLabel;
    private JTextField itemNameField;
    private JTextField itemIDLabel;
    private JTextField itemIDField;
    private JTextField priceField;
    private JTextField priceLabel;
    private JTextField quantityField;
    private JTextField quantityLabel;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField categoryLabel;
    private JTextField categoryField;
    private JTextField customerNameField;
    private JTextField customeNameLabel;
    private JTextField customerAddressLabel;
    private JTextField customerAddressField;
    private JTextField customerEmailLabel;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JTextField customerPhoneLabel;
    private JTextField customerInformationLabel;
    private JTextField productInformationLabel;
    private JTextField paymentDetailsLabel;
    private JTextField totalAmountLabel;
    private JTextField totalAmountField;
    private JTextField paymentAmountLabel;
    private JTextField paymentAmountField;
    private JTextField paymentMethodLabel;
    private JComboBox<String> paymentMethodField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

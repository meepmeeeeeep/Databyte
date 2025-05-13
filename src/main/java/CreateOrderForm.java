// AddItemForm.java

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class CreateOrderForm extends JPanel {
    private final Sales sales;
    public CreateOrderForm(Sales sales) {
        this.sales = sales;

        typingTimer = new Timer(); // Initialize typingTimer

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
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Create Order Form
    }

    private void confirm(ActionEvent e) {
        fieldsValidation();
    }

    private void fieldsValidation() {
        String itemID = itemIDField.getText().trim();
        String itemName = itemNameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String customerName = customerNameField.getText().trim();

        // Check required fields
        if (itemID.isEmpty() || itemName.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check quantity is a number
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check price is a number
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Invalid Price", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check available stock in database
        int availableStock = getAvailableStock(itemID);
        if (quantity > availableStock) {
            JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + availableStock, "Stock Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentConfirmationForm paymentForm = getPaymentConfirmationForm();

        // Open PaymentConfirmationForm
        JFrame frame = new JFrame("Payment Confirmation");
        frame.setContentPane(paymentForm);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);          // Disable window resizing
        frame.setVisible(true);

        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private PaymentConfirmationForm getPaymentConfirmationForm() {
        // Collecting data from fields
        // ---- Customer Information ----
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPhone = customerPhoneField.getText();
        String customerEmail = customerEmailField.getText();
        // ---- Product Information ----
        String itemID = itemIDField.getText();
        String itemName = itemNameField.getText();
        String category = categoryField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        // Calculate totalAmount
        double totalAmount = price * quantity;

        // Create the PaymentConfirmationForm and pass data
        return new PaymentConfirmationForm(
                customerName, customerAddress,  customerEmail, customerPhone,
                itemID, itemName, category, price, quantity, totalAmount,
                sales);
    }

    // Check available stock in Inventory
    private int getAvailableStock(String itemID) {
        int available = 0;

        String sql = "SELECT quantity FROM inventory WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                available = rs.getInt("quantity");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking stock: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return available;
    }

    private Timer typingTimer; // Create Timer Object
    private static final int TYPING_DELAY = 300; // in milliseconds

    private void itemIDFieldKeyReleased(KeyEvent e) {
        String input = itemIDField.getText();
        if (!input.isEmpty()) {
            typingTimer.cancel(); // Cancel any previous timer
            typingTimer = new Timer(); // Reset the timer
            // Schedule the query after a delay
            typingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    showSuggestions(input, itemIDField);
                }
            }, TYPING_DELAY);
        } else {
            priceField.setText("");
        }
    }

    private void itemIDFieldFocusLost(FocusEvent e) {
        suggestionMenu.setVisible(false);
    }

    private void itemNameFieldKeyReleased(KeyEvent e) {
        String input = itemNameField.getText();
        if (!input.isEmpty()) {
            typingTimer.cancel(); // Cancel any previous timer
            typingTimer = new Timer(); // Reset the timer
            // Schedule the query after a delay
            typingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    showSuggestions(input, itemNameField);
                }
            }, TYPING_DELAY);
        } else {
            priceField.setText("");
        }
    }

    private void itemNameFieldFocusLost(FocusEvent e) {
        suggestionMenu.setVisible(false);
    }

    private void categoryFieldFocusLost(FocusEvent e) {
        if (itemNameField.getText().isEmpty() || itemIDField.getText().isEmpty()) {
            priceField.setText("");
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
            dashboardLabel.setText("Create Order");
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
                        .addContainerGap(690, Short.MAX_VALUE))
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
            itemNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            itemNameField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    itemNameFieldKeyReleased(e);
                }
            });
            itemNameField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    itemNameFieldFocusLost(e);
                }
            });

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
            itemIDField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            itemIDField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    itemIDFieldKeyReleased(e);
                }
            });
            itemIDField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    itemIDFieldFocusLost(e);
                }
            });

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
            categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            categoryField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    categoryFieldFocusLost(e);
                }
            });

            //---- customerNameField ----
            customerNameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerNameField.setBackground(new Color(0xe8e7f4));
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
            customerEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerPhoneField ----
            customerPhoneField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerPhoneField.setBackground(new Color(0xe8e7f4));
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

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(productInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
                                    .addGap(30, 30, 30)
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
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
                                            .addComponent(quantityField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))))))
                        .addGap(0, 30, Short.MAX_VALUE))
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
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
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
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))
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
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    //
    // SQL Functionalities Section
    //
    // Create Pop-up Menu Object First
    JPopupMenu suggestionMenu = new JPopupMenu() {
        @Override
        public boolean isFocusable() {
            return false;
        }
    };

    // Show Suggestions on target text field based on input
    private void showSuggestions(String input, JTextField targetField) {
        suggestionMenu.removeAll();

        String sql = "SELECT item_id, item_name FROM inventory WHERE item_id LIKE ? OR item_name LIKE ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, input + "%");
            stmt.setString(2, input + "%");
            ResultSet rs = stmt.executeQuery();

            boolean foundAny = false;

            while (rs.next()) {
                foundAny = true;
                String itemId = rs.getString("item_id");
                String itemName = rs.getString("item_name");

                JMenuItem suggestion = new JMenuItem(itemId + " - " + itemName);
                suggestion.addActionListener(e -> {
                    targetField.setText(itemId);
                    fillItemDetails(itemId);
                    suggestionMenu.setVisible(false);
                });

                suggestionMenu.add(suggestion);
            }

            rs.close();

            if (foundAny) {
                suggestionMenu.show(targetField, 0, targetField.getHeight());
                suggestionMenu.revalidate();
                suggestionMenu.repaint();
            }

        } catch (SQLException e) {
            System.out.println("Suggestion Error: " + e.getMessage());
        }
    }

    // Fill text fields with the matching data from the DB (using item_id as reference)
    private void fillItemDetails(String itemId) {
        String sql = "SELECT * FROM inventory WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                itemIDField.setText(rs.getString("item_id"));
                itemNameField.setText(rs.getString("item_name"));
                categoryField.setText(rs.getString("category"));
                priceField.setText(String.valueOf(rs.getDouble("price")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Fetch by Item ID (Search by ID)
    public void fetchItemByID(String itemID) {
        String sql = "SELECT item_id, item_name, category, price FROM inventory WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                itemIDField.setText(rs.getString("item_id"));
                itemNameField.setText(rs.getString("item_name"));
                categoryField.setText(rs.getString("category"));
                priceField.setText(rs.getString("price"));
            } else {
                JOptionPane.showMessageDialog(null, "Item ID not found.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Fetch by Item Name (Search by Name)
    public void fetchItemByName(String itemName) {
        String sql = "SELECT item_id, item_name, category, price FROM inventory WHERE item_name = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                itemIDField.setText(rs.getString("item_id"));
                itemNameField.setText(rs.getString("item_name"));
                categoryField.setText(rs.getString("category"));
                priceField.setText(rs.getString("price"));
            } else {
                JOptionPane.showMessageDialog(null, "Item Name not found.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

// CreateOrderForm.java

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CreateOrderForm extends JPanel {
    private final Sales sales;
    private DefaultTableModel cartTableModel;
    private double totalPrice = 0.0;
    private Map<String, Integer> itemQuantityMap = new HashMap<>(); // To track quantities in cart
    public CreateOrderForm(Sales sales) {
        this.sales = sales;

        typingTimer = new Timer(); // Initialize typingTimer

        initComponents();
        setupCartTable();

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
        totalAmountField.setBorder(BorderFactory.createCompoundBorder(
                totalAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        vatPriceField.setBorder(BorderFactory.createCompoundBorder(
                vatPriceField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        vatAmountField.setBorder(BorderFactory.createCompoundBorder(
                vatAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        grandTotalField.setBorder(BorderFactory.createCompoundBorder(
                grandTotalField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        vatTypeField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });
    }

    private void cancelCart(ActionEvent e) {
        clearProductFields();
    }

    private void removeFromCart(ActionEvent e) {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            String itemName = (String) cartTableModel.getValueAt(selectedRow, 1);

            // Show confirmation dialog
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove '" + itemName + "' from the cart?",
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // Only remove if user clicks Yes
            if (result == JOptionPane.YES_OPTION) {
                String itemId = (String) cartTableModel.getValueAt(selectedRow, 0);
                itemQuantityMap.remove(itemId);
                cartTableModel.removeRow(selectedRow);
                updateTotalPrice();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an item to remove",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
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

    private void confirm(ActionEvent e) {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Cart is empty",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create cart data for PaymentConfirmationForm
        Object[][] cartData = new Object[cartTableModel.getRowCount()][8];
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            for (int j = 0; j < 8; j++) {
                cartData[i][j] = cartTableModel.getValueAt(i, j);
            }
        }

        // Generate transaction ID
        String transactionId = generateTransactionId();

        // Get the grand total instead of total price
        double grandTotal = Double.parseDouble(grandTotalField.getText());

        // Create and show PaymentConfirmationForm with grand total
        PaymentConfirmationForm paymentForm = new PaymentConfirmationForm(
                sales,
                transactionId,
                cartData,
                grandTotal, // Pass grand total instead of totalPrice
                customerNameField.getText(),
                customerAddressField.getText(),
                customerEmailField.getText(),
                customerPhoneField.getText()
        );

        // Create and show the frame
        JFrame frame = new JFrame("Payment Confirmation");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(paymentForm);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Dispose current frame
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void addToCart(ActionEvent e) {
        if (!validateFields()) return;

        String itemId = itemIDField.getText();

        // Validate that item details match the database
        if (!validateItemDetails(itemId)) return;

        String itemName = itemNameField.getText();
        String category = categoryField.getText();
        double basePrice = Double.parseDouble(priceField.getText());
        String vatType = vatTypeField.getSelectedItem() != null ? vatTypeField.getSelectedItem().toString() : "";
        double vatPrice = Double.parseDouble(vatPriceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        // Check available stock
        int availableStock = getAvailableStock(itemId);
        int currentCartQuantity = itemQuantityMap.getOrDefault(itemId, 0);

        if (quantity + currentCartQuantity > availableStock) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient stock. Available: " + (availableStock - currentCartQuantity),
                    "Stock Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if item already exists in cart
        int existingRow = findItemInCart(itemId);
        if (existingRow >= 0) {
            int result = JOptionPane.showConfirmDialog(this,
                    "This item is already in the cart. Would you like to merge quantities?",
                    "Merge Items", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                // Convert existing quantity from String to Integer
                int existingQuantity = Integer.parseInt(cartTableModel.getValueAt(existingRow, 6).toString());
                int newQuantity = quantity + existingQuantity;
                double subtotal = vatPrice * newQuantity;

                cartTableModel.setValueAt(newQuantity, existingRow, 6);
                cartTableModel.setValueAt(Math.round(subtotal * 100.0) / 100.0, existingRow, 7);
                itemQuantityMap.put(itemId, newQuantity);
            }
        } else {
            // Add new item to cart
            double subtotal = vatPrice * quantity;
            // Format subtotal to 2 decimal places before adding to cart
            Object[] row = {itemId, itemName, category, basePrice, vatType, vatPrice, quantity,
                    Math.round(subtotal * 100.0) / 100.0}; // Round to 2 decimal places
            cartTableModel.addRow(row);
            itemQuantityMap.put(itemId, quantity);

            // Disable Customer Information Fields
            customerNameField.setEditable(false);
            customerNameField.setFocusable(false);
            customerNameField.setBackground(Color.getColor("#fcf8ff"));
            customerPhoneField.setEditable(false);
            customerPhoneField.setFocusable(false);
            customerPhoneField.setBackground(Color.getColor("#fcf8ff"));
            customerEmailField.setEditable(false);
            customerEmailField.setFocusable(false);
            customerEmailField.setBackground(Color.getColor("#fcf8ff"));
            customerAddressField.setEditable(false);
            customerAddressField.setFocusable(false);
            customerAddressField.setBackground(Color.getColor("#fcf8ff"));
        }

        updateTotalPrice();
        clearProductFields();
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
        addToCartButton = new JButton();
        cancelCartButton = new JButton();
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
        vatPriceLabel = new JTextField();
        vatPriceField = new JTextField();
        vatTypeField = new JComboBox<>();
        vatableLabel = new JTextField();
        panel2 = new JPanel();
        cartLabel = new JTextField();
        scrollPane1 = new JScrollPane();
        cartTable = new JTable();
        confirmButton = new JButton();
        cancelButton = new JButton();
        vatAmountLabel = new JTextField();
        vatAmountField = new JTextField();
        removeItemButton = new JButton();
        totalAmountLabel2 = new JTextField();
        totalAmountField = new JTextField();
        grandTotalAmountLabel = new JTextField();
        grandTotalField = new JTextField();

        //======== this ========
        setBackground(new Color(0xe8e7f4));
        setMinimumSize(new Dimension(1400, 780));

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
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))
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
            priceLabel.setText("Unit Price:");
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

            //---- addToCartButton ----
            addToCartButton.setText("ADD");
            addToCartButton.setBackground(new Color(0x6c39c1));
            addToCartButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addToCartButton.setForeground(new Color(0xfcf8ff));
            addToCartButton.setFocusable(false);
            addToCartButton.addActionListener(e -> addToCart(e));

            //---- cancelCartButton ----
            cancelCartButton.setText("CLEAR");
            cancelCartButton.setBackground(new Color(0x6c39c1));
            cancelCartButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelCartButton.setForeground(new Color(0xfcf8ff));
            cancelCartButton.setFocusable(false);
            cancelCartButton.addActionListener(e -> cancelCart(e));

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

            //---- vatPriceLabel ----
            vatPriceLabel.setText("VAT Inc. Price:");
            vatPriceLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            vatPriceLabel.setBackground(new Color(0xfcf8ff));
            vatPriceLabel.setForeground(new Color(0x897cce));
            vatPriceLabel.setBorder(null);
            vatPriceLabel.setFocusable(false);
            vatPriceLabel.setEditable(false);

            //---- vatPriceField ----
            vatPriceField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            vatPriceField.setBackground(new Color(0xe8e7f4));
            vatPriceField.setEditable(false);
            vatPriceField.setFocusable(false);
            vatPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- vatTypeField ----
            vatTypeField.setModel(new DefaultComboBoxModel<>(new String[] {
                "VATABLE",
                "ZERO-RATED",
                "VAT EXEMPT"
            }));
            vatTypeField.setFocusable(false);
            vatTypeField.setBorder(null);
            vatTypeField.setBackground(new Color(0xe8e7f4));
            vatTypeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- vatableLabel ----
            vatableLabel.setText("VAT Type:");
            vatableLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            vatableLabel.setBackground(new Color(0xfcf8ff));
            vatableLabel.setForeground(new Color(0x897cce));
            vatableLabel.setBorder(null);
            vatableLabel.setFocusable(false);
            vatableLabel.setEditable(false);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
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
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(itemIDField, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                                    .addComponent(categoryField, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                                                .addGroup(panel1Layout.createParallelGroup()
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                        .addGap(18, 18, 18)
                                                        .addGroup(panel1Layout.createParallelGroup()
                                                            .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(quantityField, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(17, 17, 17))
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                        .addGap(17, 17, 17)
                                                        .addGroup(panel1Layout.createParallelGroup()
                                                            .addComponent(vatTypeField)
                                                            .addGroup(panel1Layout.createSequentialGroup()
                                                                .addComponent(vatableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)))
                                                        .addGap(18, 18, 18))))
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(itemNameField)
                                                .addGap(18, 18, 18)))
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(vatPriceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(priceField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(vatPriceField)
                                                .addGroup(panel1Layout.createSequentialGroup()
                                                    .addComponent(addToCartButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                    .addGap(20, 20, 20)
                                                    .addComponent(cancelCartButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(productInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
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
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(productInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(priceField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemNameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(vatPriceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(vatPriceField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(cancelCartButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(addToCartButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(quantityField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(vatableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vatTypeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(14, 14, 14))
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
                scrollPane1.setPreferredSize(new Dimension(800, 400));

                //---- cartTable ----
                cartTable.setFillsViewportHeight(true);
                cartTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                scrollPane1.setViewportView(cartTable);
            }

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

            //---- vatAmountLabel ----
            vatAmountLabel.setText("VAT Amount:");
            vatAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            vatAmountLabel.setBackground(new Color(0xfcf8ff));
            vatAmountLabel.setForeground(new Color(0x897cce));
            vatAmountLabel.setBorder(null);
            vatAmountLabel.setFocusable(false);
            vatAmountLabel.setEditable(false);

            //---- vatAmountField ----
            vatAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            vatAmountField.setBackground(new Color(0xe8e7f4));
            vatAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vatAmountField.setEditable(false);
            vatAmountField.setFocusable(false);

            //---- removeItemButton ----
            removeItemButton.setText("REMOVE");
            removeItemButton.setBackground(new Color(0x6c39c1));
            removeItemButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            removeItemButton.setForeground(new Color(0xfcf8ff));
            removeItemButton.setFocusable(false);
            removeItemButton.addActionListener(e -> removeFromCart(e));

            //---- totalAmountLabel2 ----
            totalAmountLabel2.setText("Total Amount:");
            totalAmountLabel2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            totalAmountLabel2.setBackground(new Color(0xfcf8ff));
            totalAmountLabel2.setForeground(new Color(0x897cce));
            totalAmountLabel2.setBorder(null);
            totalAmountLabel2.setFocusable(false);
            totalAmountLabel2.setEditable(false);

            //---- totalAmountField ----
            totalAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            totalAmountField.setBackground(new Color(0xe8e7f4));
            totalAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            totalAmountField.setEditable(false);
            totalAmountField.setFocusable(false);

            //---- grandTotalAmountLabel ----
            grandTotalAmountLabel.setText("Grand Total Amount:");
            grandTotalAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            grandTotalAmountLabel.setBackground(new Color(0xfcf8ff));
            grandTotalAmountLabel.setForeground(new Color(0x897cce));
            grandTotalAmountLabel.setBorder(null);
            grandTotalAmountLabel.setFocusable(false);
            grandTotalAmountLabel.setEditable(false);

            //---- grandTotalField ----
            grandTotalField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            grandTotalField.setBackground(new Color(0xe8e7f4));
            grandTotalField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            grandTotalField.setEditable(false);
            grandTotalField.setFocusable(false);

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addComponent(vatAmountField, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                            .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 341, Short.MAX_VALUE)
                                .addComponent(removeItemButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(vatAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 406, Short.MAX_VALUE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(totalAmountLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(grandTotalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(grandTotalField, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))))
                        .addGap(20, 20, 20))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(removeItemButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 423, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(totalAmountLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(grandTotalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(grandTotalField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(vatAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(vatAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11))
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
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
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
    private JButton addToCartButton;
    private JButton cancelCartButton;
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
    private JTextField vatPriceLabel;
    private JTextField vatPriceField;
    private JComboBox<String> vatTypeField;
    private JTextField vatableLabel;
    private JPanel panel2;
    private JTextField cartLabel;
    private JScrollPane scrollPane1;
    private JTable cartTable;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField vatAmountLabel;
    private JTextField vatAmountField;
    private JButton removeItemButton;
    private JTextField totalAmountLabel2;
    private JTextField totalAmountField;
    private JTextField grandTotalAmountLabel;
    private JTextField grandTotalField;
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
                double basePrice = rs.getDouble("price");
                String vatType = rs.getString("vat_type");
                vatTypeField.setSelectedItem(vatType);
                double vatPrice = rs.getDouble("vat_inclusive_price");

                priceField.setText(String.format("%.2f", basePrice));
                vatPriceField.setText(String.format("%.2f", vatPrice));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving item details: " + e.getMessage());
        }
    }

    private void clearProductFields() {
        itemNameField.setText("");
        itemIDField.setText("");
        categoryField.setText("");
        priceField.setText("");
        quantityField.setText("");
        vatPriceField.setText("");
    }

    private void updateTotalPrice() {
        double totalBasePrice = 0.0;
        double totalVatAmount = 0.0;

        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            double basePrice = (double) cartTableModel.getValueAt(i, 3);
            double vatPrice = (double) cartTableModel.getValueAt(i, 5);
            int quantity = (int) cartTableModel.getValueAt(i, 6);

            totalBasePrice += basePrice * quantity;
            totalVatAmount += (vatPrice - basePrice) * quantity;
        }

        totalAmountField.setText(String.format("%.2f", totalBasePrice));
        vatAmountField.setText(String.format("%.2f", totalVatAmount));
        grandTotalField.setText(String.format("%.2f", totalBasePrice + totalVatAmount));

    }

    private int findItemInCart(String itemId) {
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (itemId.equals(cartTableModel.getValueAt(i, 0))) {
                return i;
            }
        }
        return -1;
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private void setupCartTable() {
        // Set up cart table model with columns
        String[] columns = {"Item ID", "Item Name", "Category", "Unit Price", "VAT Type", "VAT Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        // Modern scroll bar styling
        scrollPane1.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        int[] columnWidths = {100, 180, 120, 100, 100, 100, 80, 120};
        TableColumnModel columnModel = cartTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(columnWidths[i]);
            column.setPreferredWidth(columnWidths[i]);
            column.setMaxWidth(columnWidths[i]);
        }

        // Calculate total width
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width;
        }

        // Set preferred size for the table
        cartTable.setPreferredScrollableViewportSize(new Dimension(totalWidth, 400));

        // Set scroll pane properties with unified scroll bar appearance
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set minimum and preferred column widths
        for (int i = 0; i < cartTable.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(100);  // Minimum width for each column
            column.setPreferredWidth(150);  // Preferred width for each column
        }

        // Make the table fill the viewport
        cartTable.setFillsViewportHeight(true);

        // Set preferred size for the scroll pane
        scrollPane1.setPreferredSize(new Dimension(430, 400));
    }

    private boolean validateFields() {
        // Validate customer name (required)
        if (customerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Customer name is required",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate product fields
        if (itemIDField.getText().trim().isEmpty() ||
                itemNameField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All product fields are required",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate quantity is a positive number
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity must be greater than 0",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a valid number",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate price is a positive number
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Price must be greater than 0",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validateItemDetails(String itemId) {
        String sql = "SELECT * FROM inventory WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Compare all fields with database values
                boolean isValid =
                        itemNameField.getText().equals(rs.getString("item_name")) &&
                                categoryField.getText().equals(rs.getString("category")) &&
                                String.format("%.2f", Double.parseDouble(priceField.getText())).equals(String.format("%.2f", rs.getDouble("price"))) &&
                                vatTypeField.getSelectedItem().toString().equals(rs.getString("vat_type")) &&
                                String.format("%.2f", Double.parseDouble(vatPriceField.getText())).equals(String.format("%.2f", rs.getDouble("vat_inclusive_price")));

                if (!isValid) {
                    JOptionPane.showMessageDialog(this,
                            "Item details have been modified. Please use the auto-suggestion to fill correct item details.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                }
                return isValid;
            }

            JOptionPane.showMessageDialog(this,
                    "Item not found in inventory.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error validating item details: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

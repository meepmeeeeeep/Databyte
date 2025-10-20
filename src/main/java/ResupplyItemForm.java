// EditItemForm.java

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ResupplyItemForm extends JPanel {
    private final String originalItemId;
    private final Resupply resupply;

    public ResupplyItemForm(String itemId, Resupply resupply) {
        this.resupply = resupply;
        // Store the Original Item ID Value
        this.originalItemId = itemId;

        initComponents();
        setupTotalCostCalculation();

        // Add Left-Padding to Text Fields
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
        supplierNameFIeld.setBorder(BorderFactory.createCompoundBorder(
                supplierNameFIeld.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        supplierAddressField.setBorder(BorderFactory.createCompoundBorder(
                supplierAddressField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        supplierContactNumberField.setBorder(BorderFactory.createCompoundBorder(
                supplierContactNumberField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        unitCostField.setBorder(BorderFactory.createCompoundBorder(
                unitCostField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        totalCostField.setBorder(BorderFactory.createCompoundBorder(
                totalCostField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));

        fetchDBData(itemId);
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Add Item Form
    }

    private void edit(ActionEvent e) {
        // Get the updated values from the text fields
        String updatedItemID = itemIDField.getText();
        String updatedItemName = itemNameField.getText();
        String updatedCategory = categoryField.getText();
        String updatedQuantity = quantityField.getText();
        String updatedPrice = priceField.getText();

        // Get resupply specific information
        String supplierName = supplierNameFIeld.getText();
        String supplierAddress = supplierAddressField.getText();
        String supplierContactNumber = supplierContactNumberField.getText();
        String unitCost = unitCostField.getText();

        // Validate if required fields are filled
        if (updatedItemName.isEmpty() || updatedCategory.isEmpty() || updatedQuantity.isEmpty()
                || updatedPrice.isEmpty() || supplierName.isEmpty() || unitCost.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        // Update the item in the database
        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            double price = Double.parseDouble(updatedPrice);
            int quantity = Integer.parseInt(updatedQuantity);
            double unitCostValue = Double.parseDouble(unitCost);
            double totalCostValue = unitCostValue * quantity;

            // Update the total cost field
            totalCostField.setText(String.format("%.2f", totalCostValue));

            if (quantity <= 0 || price <= 0 || unitCostValue <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity, Price, and Unit Cost must be positive numbers.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Update inventory table
            String updateInventorySql = "UPDATE inventory SET item_id = ?, item_name = ?, category = ?, quantity = quantity + ?, price = ? WHERE item_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateInventorySql)) {
                pstmt.setString(1, updatedItemID);
                pstmt.setString(2, updatedItemName);
                pstmt.setString(3, updatedCategory);
                pstmt.setInt(4, quantity);
                pstmt.setDouble(5, price);
                pstmt.setString(6, originalItemId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected <= 0) {
                    throw new SQLException("Failed to update inventory item");
                }
            }

            // 2. Insert into resupply_history table (use timestamp with time)
            String insertResupplySql = "INSERT INTO resupply_history (item_id, item_name, quantity, supplier_name, " +
                    "supplier_address, supplier_contact, unit_cost, total_cost, resupply_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertResupplySql)) {
                pstmt.setString(1, updatedItemID);
                pstmt.setString(2, updatedItemName);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, supplierName);
                pstmt.setString(5, supplierAddress);
                pstmt.setString(6, supplierContactNumber);
                pstmt.setDouble(7, unitCostValue);
                pstmt.setDouble(8, totalCostValue);
                // bind current timestamp (includes date and time, e.g. 2025-10-14 01:11:23)
                pstmt.setTimestamp(9, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction if both operations successful

            JOptionPane.showMessageDialog(this, "Item restocked successfully.");
            resupply.populateTable(); // Update inventory table
            resupply.populateResupplyHistory(); // Update resupply history table
            SwingUtilities.getWindowAncestor(this).dispose(); // Close the form

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price, Quantity, and Cost fields must be numeric.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(),
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
            try {
                // Rollback transaction on error
                Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                JOptionPane.showMessageDialog(this, "Error during rollback: " + rollbackEx.getMessage());
            }
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
        addButton = new JButton();
        cancelButton = new JButton();
        categoryLabel = new JTextField();
        categoryField = new JTextField();
        supplierNameLabel = new JTextField();
        supplierNameFIeld = new JTextField();
        supplierAddressLabel = new JTextField();
        supplierAddressField = new JTextField();
        supplierContactNumberLabel = new JTextField();
        supplierContactNumberField = new JTextField();
        unitCostLabel = new JTextField();
        unitCostField = new JTextField();
        totalCostLabel = new JTextField();
        totalCostField = new JTextField();
        resupplyInformationLabel = new JTextField();
        itemInformationLabel = new JTextField();

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
                    .addGap(0, 745, Short.MAX_VALUE)
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Resupply Item");
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
                        .addContainerGap(677, Short.MAX_VALUE))
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
            itemNameField.setBackground(new Color(0xfcf8ff));
            itemNameField.setEditable(false);

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
            itemIDField.setBackground(new Color(0xfcf8ff));
            itemIDField.setEditable(false);

            //---- priceField ----
            priceField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            priceField.setEditable(false);
            priceField.setBackground(new Color(0xfcf8ff));

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

            //---- quantityLabel ----
            quantityLabel.setText("Quantity:");
            quantityLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            quantityLabel.setBackground(new Color(0xfcf8ff));
            quantityLabel.setForeground(new Color(0x897cce));
            quantityLabel.setBorder(null);
            quantityLabel.setFocusable(false);
            quantityLabel.setEditable(false);

            //---- addButton ----
            addButton.setText("CONFIRM");
            addButton.setBackground(new Color(0x6c39c1));
            addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addButton.setForeground(new Color(0xfcf8ff));
            addButton.setFocusable(false);
            addButton.addActionListener(e -> edit(e));

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
            categoryField.setBackground(new Color(0xfcf8ff));
            categoryField.setEditable(false);

            //---- supplierNameLabel ----
            supplierNameLabel.setText("Supplier Name:");
            supplierNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            supplierNameLabel.setBackground(new Color(0xfcf8ff));
            supplierNameLabel.setForeground(new Color(0x897cce));
            supplierNameLabel.setBorder(null);
            supplierNameLabel.setFocusable(false);
            supplierNameLabel.setEditable(false);

            //---- supplierNameFIeld ----
            supplierNameFIeld.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            supplierNameFIeld.setBackground(new Color(0xe8e7f4));

            //---- supplierAddressLabel ----
            supplierAddressLabel.setText("Supplier Address: (Optional)");
            supplierAddressLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            supplierAddressLabel.setBackground(new Color(0xfcf8ff));
            supplierAddressLabel.setForeground(new Color(0x897cce));
            supplierAddressLabel.setBorder(null);
            supplierAddressLabel.setFocusable(false);
            supplierAddressLabel.setEditable(false);

            //---- supplierAddressField ----
            supplierAddressField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            supplierAddressField.setBackground(new Color(0xe8e7f4));

            //---- supplierContactNumberLabel ----
            supplierContactNumberLabel.setText("Supplier Contact Number: (Optional)");
            supplierContactNumberLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            supplierContactNumberLabel.setBackground(new Color(0xfcf8ff));
            supplierContactNumberLabel.setForeground(new Color(0x897cce));
            supplierContactNumberLabel.setBorder(null);
            supplierContactNumberLabel.setFocusable(false);
            supplierContactNumberLabel.setEditable(false);

            //---- supplierContactNumberField ----
            supplierContactNumberField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            supplierContactNumberField.setBackground(new Color(0xe8e7f4));

            //---- unitCostLabel ----
            unitCostLabel.setText("Unit Cost:");
            unitCostLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            unitCostLabel.setBackground(new Color(0xfcf8ff));
            unitCostLabel.setForeground(new Color(0x897cce));
            unitCostLabel.setBorder(null);
            unitCostLabel.setFocusable(false);
            unitCostLabel.setEditable(false);

            //---- unitCostField ----
            unitCostField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            unitCostField.setBackground(new Color(0xe8e7f4));

            //---- totalCostLabel ----
            totalCostLabel.setText("Total Cost:");
            totalCostLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            totalCostLabel.setBackground(new Color(0xfcf8ff));
            totalCostLabel.setForeground(new Color(0x897cce));
            totalCostLabel.setBorder(null);
            totalCostLabel.setFocusable(false);
            totalCostLabel.setEditable(false);

            //---- totalCostField ----
            totalCostField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            totalCostField.setBackground(new Color(0xfcf8ff));
            totalCostField.setEditable(false);

            //---- resupplyInformationLabel ----
            resupplyInformationLabel.setText("Resupply Information");
            resupplyInformationLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            resupplyInformationLabel.setBackground(new Color(0xfcf8ff));
            resupplyInformationLabel.setForeground(new Color(0x251779));
            resupplyInformationLabel.setBorder(null);
            resupplyInformationLabel.setFocusable(false);
            resupplyInformationLabel.setEditable(false);

            //---- itemInformationLabel ----
            itemInformationLabel.setText("Item Information");
            itemInformationLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            itemInformationLabel.setBackground(new Color(0xfcf8ff));
            itemInformationLabel.setForeground(new Color(0x251779));
            itemInformationLabel.setBorder(null);
            itemInformationLabel.setFocusable(false);
            itemInformationLabel.setEditable(false);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(itemNameField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(itemInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(50, 50, 50)
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(priceField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(supplierNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(supplierNameFIeld, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(supplierAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(supplierAddressField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(supplierContactNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(supplierContactNumberField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(resupplyInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(50, 50, 50)
                                    .addGroup(panel1Layout.createParallelGroup()
                                        .addComponent(totalCostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(totalCostField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(unitCostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(unitCostField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(quantityField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(25, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap(11, Short.MAX_VALUE)
                        .addComponent(itemInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(resupplyInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(supplierNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(supplierNameFIeld, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(quantityField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(supplierAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(supplierAddressField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(unitCostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(unitCostField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(supplierContactNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(supplierContactNumberField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(totalCostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(totalCostField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27))
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
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
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
    private JButton addButton;
    private JButton cancelButton;
    private JTextField categoryLabel;
    private JTextField categoryField;
    private JTextField supplierNameLabel;
    private JTextField supplierNameFIeld;
    private JTextField supplierAddressLabel;
    private JTextField supplierAddressField;
    private JTextField supplierContactNumberLabel;
    private JTextField supplierContactNumberField;
    private JTextField unitCostLabel;
    private JTextField unitCostField;
    private JTextField totalCostLabel;
    private JTextField totalCostField;
    private JTextField resupplyInformationLabel;
    private JTextField itemInformationLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void fetchDBData(String itemId) {
        // Fetch item data from DB
        try {
            Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
            String sql = "SELECT * FROM inventory WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Fill text fields with fetched data
                itemIDField.setText(rs.getString("item_id"));
                itemNameField.setText(rs.getString("item_name"));
                categoryField.setText(rs.getString("category"));
                priceField.setText(rs.getString("price"));
            } else {
                JOptionPane.showMessageDialog(this, "Item not found in database.");
                SwingUtilities.getWindowAncestor(this).dispose();
            }

            pstmt.close();
            rs.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading item: " + e.getMessage());
            System.out.println(e.getMessage());
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    // Add this method to your ResupplyItemForm class
    private void setupTotalCostCalculation() {
        // DocumentListener to update total cost when unit cost or quantity changes
        DocumentListener calculationListener = new DocumentListener() {
            private void updateTotalCost() {
                try {
                    // Get the values from the fields
                    String quantityStr = quantityField.getText().trim();
                    String unitCostStr = unitCostField.getText().trim();

                    // Check if both fields have values
                    if (!quantityStr.isEmpty() && !unitCostStr.isEmpty()) {
                        double quantity = Double.parseDouble(quantityStr);
                        double unitCost = Double.parseDouble(unitCostStr);
                        double totalCost = quantity * unitCost;

                        // Update the total cost field
                        totalCostField.setText(String.format("%.2f", totalCost));
                    } else {
                        // Clear the total cost field if either input is empty
                        totalCostField.setText("");
                    }
                } catch (NumberFormatException e) {
                    // Handle parsing errors
                    totalCostField.setText("Invalid input");
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTotalCost();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTotalCost();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTotalCost();
            }
        };

        // Add the listener to both fields
        quantityField.getDocument().addDocumentListener(calculationListener);
        unitCostField.getDocument().addDocumentListener(calculationListener);
    }

}

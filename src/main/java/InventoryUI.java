// InventoryUI.java

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class InventoryUI {
    private JPanel rootPanel;
    private JComboBox<String> categoryComboBox;
    private JTextField itemIdField;
    private JTextField brandField;
    private JTextField itemNameField;
    private JTextField priceField;
    private JTextField stockCountField;
    private JLabel categoryLabel;
    private JLabel itemIdLabel;
    private JLabel brandLabel;
    private JLabel itemNameLabel;
    private JLabel priceLabel;
    private JLabel stockCountLabel;
    private JPanel tableButtonsContainer;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JScrollPane inventoryTableScrollPane;
    private JTable inventoryTable;

    public InventoryUI() {
        // Create the table model with column names
        String[] columnNames = {"Item ID", "Category", "Brand Name", "Item Name", "Price", "Stock Count", "Stock Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        inventoryTable.setModel(model);  // Set the table's model

        // Fetch and populate inventory data into the table
        refreshTable(model);

        // Button Action to refresh the table
        refreshButton.addActionListener(e -> refreshTable(model));

        // Add button functionality
        addButton.addActionListener(e -> {
            // Retrieve values from input fields
            String category = (String) categoryComboBox.getSelectedItem();
            String itemId = itemIdField.getText().trim();
            String brand = brandField.getText().trim();
            String itemName = itemNameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockCountText = stockCountField.getText().trim();

            // Validate input (you can add more validation)
            if (itemId.isEmpty() || brand.isEmpty() || itemName.isEmpty() || priceText.isEmpty() || stockCountText.isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel, "All fields must be filled in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Parse price and stock count
                double price = Double.parseDouble(priceText);
                int stockCount = Integer.parseInt(stockCountText);

                // Create new InventoryItem
                InventoryItem newItem = new InventoryItem(category, itemId, brand, itemName, price, stockCount);

                // Insert item into the database
                addItemToDatabase(newItem);

                // Refresh the table to show the newly added item
                refreshTable(model);

                JOptionPane.showMessageDialog(rootPanel, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(rootPanel, "Invalid input for price or stock count.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit Button Functionality
        editButton.addActionListener(e -> editSelectedItem());

        // Auto-Fill Text Fields
        inventoryTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && inventoryTable.getSelectedRow() != -1) {
                int row = inventoryTable.getSelectedRow();
                itemIdField.setText(inventoryTable.getValueAt(row, 0).toString());
                categoryComboBox.setSelectedItem(inventoryTable.getValueAt(row, 1).toString());
                brandField.setText(inventoryTable.getValueAt(row, 2).toString());
                itemNameField.setText(inventoryTable.getValueAt(row, 3).toString());
                priceField.setText(inventoryTable.getValueAt(row, 4).toString());
                stockCountField.setText(inventoryTable.getValueAt(row, 5).toString());
                itemIdField.setEditable(false);
            }
        });

        // Delete Button Functionality
        deleteButton.addActionListener(e -> deleteSelectedItem());

    }

    private void addItemToDatabase(InventoryItem newItem) {
        String insertSQL = "INSERT INTO inventory (item_id, category, brand_name, item_name, item_price, item_stock, stock_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setString(1, newItem.itemId);
            stmt.setString(2, newItem.category);
            stmt.setString(3, newItem.brandName);
            stmt.setString(4, newItem.itemName);
            stmt.setDouble(5, newItem.price);
            stmt.setInt(6, newItem.stockCount);
            stmt.setString(7, newItem.stockStatus);

            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(rootPanel, "Error inserting item into database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to edit.");
            return;
        }

        String itemId = itemIdField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        String brand = brandField.getText().trim();
        String itemName = itemNameField.getText().trim();
        double price;
        int stock;

        try {
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockCountField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid price or stock count.");
            return;
        }

        String stockStatus = calculateStockStatus(stock);

        try (Connection conn = DatabaseManager.getConnection()) {
            String updateSQL = """
            UPDATE inventory
            SET category = ?, brand_name = ?, item_name = ?, item_price = ?, item_stock = ?, stock_status = ?
            WHERE item_id = ?;
            """;

            PreparedStatement pstmt = conn.prepareStatement(updateSQL);
            pstmt.setString(1, category);
            pstmt.setString(2, brand);
            pstmt.setString(3, itemName);
            pstmt.setDouble(4, price);
            pstmt.setInt(5, stock);
            pstmt.setString(6, stockStatus);
            pstmt.setString(7, itemId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Item updated successfully.");
                refreshTable((DefaultTableModel) inventoryTable.getModel()); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update item.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace(); // Print full details of the SQL Error
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to delete.");
            return;
        }

        String itemId = (String) inventoryTable.getValueAt(selectedRow, 0); // assuming item_id is column 0

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete Item ID: " + itemId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String deleteSQL = "DELETE FROM inventory WHERE item_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(deleteSQL);
                pstmt.setString(1, itemId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Item deleted successfully.");
                    refreshTable((DefaultTableModel) inventoryTable.getModel());
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete item.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace(); // Print full details of the SQL Error
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        }
    }

    private void refreshTable(DefaultTableModel model) {
        // Clear any previous data
        model.setRowCount(0);

        // Get the inventory items from the database
        List<InventoryItem> items = DatabaseManager.getAllInventoryItems();

        // Add data to the table
        for (InventoryItem item : items) {
            Object[] row = {
                    item.itemId,
                    item.category,
                    item.brandName,
                    item.itemName,
                    item.price,
                    item.stockCount,
                    item.stockStatus
            };
            model.addRow(row);
        }

        // Clear Fields and Selected Row
        inventoryTable.clearSelection();
        clearFields();
    }

    private static String calculateStockStatus(int count) {
        if (count == 0) return "Out of Stock";
        else if (count <= 10) return "Low Stock";
        else return "In Stock";
    }

    private void clearFields() {
        itemIdField.setText("");
        brandField.setText("");
        itemNameField.setText("");
        priceField.setText("");
        stockCountField.setText("");
        categoryComboBox.setSelectedIndex(-1);
        itemIdField.setEditable(true);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}

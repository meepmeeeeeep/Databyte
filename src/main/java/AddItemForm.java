// AddItemForm.java

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
public class AddItemForm extends JPanel {
    private final Inventory inventory;

    public AddItemForm(Inventory inventory) {
        this.inventory = inventory;

        initComponents();

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
        vatableField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Add Item Form
    }

    private void add(ActionEvent e) {
        String itemName = itemNameField.getText().trim();
        String itemID = itemIDField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();

        // Validation
        if (itemName.isEmpty() || itemID.isEmpty() || category.isEmpty() ||
                priceText.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            if (quantity <= 0 || price <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price must be positive numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO inventory (item_id, item_name, category, quantity, price, vat_type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, itemID);
            pstmt.setString(2, itemName);
            pstmt.setString(3, category);
            pstmt.setInt(4, quantity);
            pstmt.setDouble(5, price);
            pstmt.setString(6, vatableField.getSelectedItem().toString());

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Item added successfully!");
                inventory.populateTable();
                SwingUtilities.getWindowAncestor(this).dispose(); // Close the form
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add item.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pstmt.close();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
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
        vatableLabel = new JTextField();
        vatableField = new JComboBox<>();

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
            dashboardLabel.setText("Add Item");
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
                        .addContainerGap(721, Short.MAX_VALUE))
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

            //---- priceField ----
            priceField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            priceField.setBackground(new Color(0xe8e7f4));

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
            addButton.setText("ADD");
            addButton.setBackground(new Color(0x6c39c1));
            addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addButton.setForeground(new Color(0xfcf8ff));
            addButton.setFocusable(false);
            addButton.addActionListener(e -> add(e));

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

            //---- vatableLabel ----
            vatableLabel.setText("VAT Type:");
            vatableLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            vatableLabel.setBackground(new Color(0xfcf8ff));
            vatableLabel.setForeground(new Color(0x897cce));
            vatableLabel.setBorder(null);
            vatableLabel.setFocusable(false);
            vatableLabel.setEditable(false);

            //---- vatableField ----
            vatableField.setModel(new DefaultComboBoxModel<>(new String[] {
                "VATABLE",
                "VAT EXEMPT"
            }));
            vatableField.setFocusable(false);
            vatableField.setBorder(null);
            vatableField.setBackground(new Color(0xe8e7f4));
            vatableField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(itemNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(itemNameField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(itemIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(itemIDField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(quantityLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(priceLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(priceField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addComponent(quantityField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                            .addComponent(vatableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(vatableField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(25, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
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
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(categoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(vatableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(categoryField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(vatableField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
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
    private JTextField vatableLabel;
    private JComboBox<String> vatableField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

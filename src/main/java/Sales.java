// Sales.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class Sales extends JPanel {
    public Sales() {
        // Use Custom Background Images for Side Panel Buttons
        //---- dashboardButton ----
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButton.png"))).getImage();
        dashboardButton = new ImageButton(dashboardBg, "");

        //---- inventoryButton ----
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButton.png"))).getImage();
        inventoryButton = new ImageButton(inventoryBg, "");

        //---- salesButton ----
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButtonActive.png"))).getImage();
        salesButton = new ImageButton(salesBg, "");

        //---- financialsButton ----
        Image financialsBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/financialsButton.png"))).getImage();
        financialsButton = new ImageButton(financialsBg, "");

        //---- resupplyButton ----
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButton.png"))).getImage();
        resupplyButton = new ImageButton(resupplyBg, "");

        //---- exitButton ----
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButton.png"))).getImage();
        exitButton = new ImageButton(exitBg, "");

        initComponents();

        String[] columns = {"Sale ID", "Date", "Customer Name", "Item ID", "Item Name", "Quantity", "Unit Price", "Total Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        transactionHistoryTable.setModel(tableModel);

        setTableTheme();

        populateTableSales(); // Load the table data
        searchListenerHandler(); // Initialize Search Listener Handler

        // Add Left-Padding to Search Field
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
    }

    //
    // Exit Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void exitButtonMouseEntered(MouseEvent e) {
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButtonActive.png"))).getImage();
        ((ImageButton) exitButton).setBackgroundImage(exitBg);
    }
    // Hover Effects - Mouse Exit
    private void exitButtonMouseExited(MouseEvent e) {
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButton.png"))).getImage();
        ((ImageButton) exitButton).setBackgroundImage(exitBg);
    }
    // Hover Effects - Mouse Press
    private void exitButtonMousePressed(MouseEvent e) {
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButtonPressed.png"))).getImage();
        ((ImageButton) exitButton).setBackgroundImage(exitBg);
    }
    // Action Listener Method
    private void exit(ActionEvent e) {
        System.exit(0); // Exit Window
    }

    //
    // Dashboard Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void dashboardButtonMouseEntered(MouseEvent e) {
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButtonActive.png"))).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Hover Effects - Mouse Exit
    private void dashboardButtonMouseExited(MouseEvent e) {
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButton.png"))).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Hover Effects - Mouse Press
    private void dashboardButtonMousePressed(MouseEvent e) {
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButtonPressed.png"))).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Action Listener Method
    private void dashboard(ActionEvent e) {
        // Open Dashboard
        SwingUtilities.getWindowAncestor(this).dispose();; // Close Sales

        JFrame frame = new JFrame("Dashboard");
        frame.setContentPane(new Dashboard());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Inventory Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void inventoryButtonMouseEntered(MouseEvent e) {
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButtonActive.png"))).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Hover Effects - Mouse Exit
    private void inventoryButtonMouseExited(MouseEvent e) {
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButton.png"))).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Hover Effects - Mouse Press
    private void inventoryButtonMousePressed(MouseEvent e) {
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButtonPressed.png"))).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Action Listener Method
    private void inventory(ActionEvent e) {
        // Open Inventory
        SwingUtilities.getWindowAncestor(this).dispose();; // Close Sales

        JFrame frame = new JFrame("Inventory");
        frame.setContentPane(new Inventory());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Sales Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void salesButtonMouseEntered(MouseEvent e) {
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButtonActive.png"))).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Hover Effects - Mouse Exit
    private void salesButtonMouseExited(MouseEvent e) {
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButtonActive.png"))).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Hover Effects - Mouse Press
    private void salesButtonMousePressed(MouseEvent e) {
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButtonActive.png"))).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }

    //
    // Financials Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void financialsButtonMouseEntered(MouseEvent e) {
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButtonActive.png")).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Hover Effects - Mouse Exit
    private void financialsButtonMouseExited(MouseEvent e) {
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButton.png")).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Hover Effects - Mouse Press
    private void financialsButtonMousePressed(MouseEvent e) {
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButtonPressed.png")).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Action Listener Method
    private void financials(ActionEvent e) {
        // Open Financials
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Sales

        JFrame frame = new JFrame("Financials");
        frame.setContentPane(new Financials());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Resupply Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void resupplyButtonMouseEntered(MouseEvent e) {
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButtonActive.png"))).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Hover Effects - Mouse Exit
    private void resupplyButtonMouseExited(MouseEvent e) {
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButton.png"))).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Hover Effects - Mouse Press
    private void resupplyButtonMousePressed(MouseEvent e) {
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButtonPressed.png"))).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Action Listener Method
    private void resupply(ActionEvent e) {
        // Open Resupply
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Sales

        JFrame frame = new JFrame("Resupply");
        frame.setContentPane(new Resupply());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Create Order Button Event Listener Methods
    //
    // Action Listener Method
    private void viewDetails(ActionEvent e) {
        int selectedRow = transactionHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a transaction to view details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String transactionId = transactionHistoryTable.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            // Get transaction details
            String transactionSql = "SELECT * FROM transaction_history WHERE transaction_id = ?";
            PreparedStatement transactionStmt = conn.prepareStatement(transactionSql);
            transactionStmt.setString(1, transactionId);
            ResultSet transactionRs = transactionStmt.executeQuery();

            if (transactionRs.next()) {
                // Get cart items
                String cartSql = "SELECT * FROM cart_items WHERE transaction_id = ?";
                PreparedStatement cartStmt = conn.prepareStatement(cartSql);
                cartStmt.setString(1, transactionId);
                ResultSet cartRs = cartStmt.executeQuery();

                // Convert ResultSet to ArrayList
                java.util.List<Object[]> cartData = new ArrayList<>();
                while (cartRs.next()) {
                    Object[] row = {
                            cartRs.getString("item_id"),
                            cartRs.getString("item_name"),
                            cartRs.getString("category"),
                            cartRs.getDouble("price"),
                            cartRs.getString("vat_type"),
                            cartRs.getDouble("vat_inclusive_price"),
                            cartRs.getInt("quantity"),
                            cartRs.getDouble("price") * cartRs.getInt("quantity")
                    };
                    cartData.add(row);
                }

                // Create and show OrderDetailsForm
                OrderDetailsForm detailsForm = new OrderDetailsForm(
                        transactionId,
                        transactionRs.getTimestamp("date"),
                        transactionRs.getString("customer_name"),
                        transactionRs.getString("customer_address"),
                        transactionRs.getString("customer_email"),
                        transactionRs.getString("customer_phone"),
                        transactionRs.getDouble("total_price"),
                        transactionRs.getDouble("payment_amount"),
                        transactionRs.getString("payment_method"),
                        transactionRs.getString("discount_code"),
                        cartData.toArray(new Object[cartData.size()][])
                );

                JFrame frame = new JFrame("Order Details");
                frame.setContentPane(detailsForm);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading order details: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createOrder(ActionEvent e) {
        JFrame frame = new JFrame("Create Order");
        frame.setContentPane(new CreateOrderForm(this));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);          // Disable window resizing
        frame.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        appNameLabel = new JLabel();
        appNameSubLabel = new JLabel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        scrollPane1 = new JScrollPane();
        transactionHistoryTable = new JTable();
        controlsPanel = new JPanel();
        transactionHistoryLabel = new JTextField();
        createOrderButton = new JButton();
        searchField = new JTextField();
        viewDetailsButton = new JButton();

        //======== this ========
        setBackground(new Color(0xe8e7f4));

        //======== sidePanel ========
        {
            sidePanel.setBackground(new Color(0x6c39c1));
            sidePanel.setMaximumSize(new Dimension(260, 32823));
            sidePanel.setMinimumSize(new Dimension(260, 62));
            sidePanel.setPreferredSize(new Dimension(260, 820));

            //---- appNameLabel ----
            appNameLabel.setText("Databyte");
            appNameLabel.setFont(new Font("Segoe UI Black", Font.ITALIC, 30));
            appNameLabel.setForeground(Color.white);
            appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            //---- appNameSubLabel ----
            appNameSubLabel.setText("by group 7");
            appNameSubLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            appNameSubLabel.setForeground(Color.white);
            appNameSubLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            //---- dashboardButton ----
            dashboardButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            dashboardButton.setForeground(new Color(0x6c39c1));
            dashboardButton.setBackground(new Color(0x6c39c1));
            dashboardButton.setBorder(null);
            dashboardButton.setHorizontalAlignment(SwingConstants.LEFT);
            dashboardButton.setFocusable(false);
            dashboardButton.setBorderPainted(false);
            dashboardButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dashboardButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    dashboardButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    dashboardButtonMousePressed(e);
                }
            });
            dashboardButton.addActionListener(e -> dashboard(e));

            //---- inventoryButton ----
            inventoryButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            inventoryButton.setForeground(new Color(0x6c39c1));
            inventoryButton.setBackground(new Color(0x6c39c1));
            inventoryButton.setBorder(null);
            inventoryButton.setHorizontalAlignment(SwingConstants.LEFT);
            inventoryButton.setFocusable(false);
            inventoryButton.setBorderPainted(false);
            inventoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            inventoryButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    inventoryButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    inventoryButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    inventoryButtonMousePressed(e);
                }
            });
            inventoryButton.addActionListener(e -> inventory(e));

            //---- salesButton ----
            salesButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            salesButton.setForeground(new Color(0x6c39c1));
            salesButton.setBackground(new Color(0x6c39c1));
            salesButton.setBorder(null);
            salesButton.setHorizontalAlignment(SwingConstants.LEFT);
            salesButton.setFocusable(false);
            salesButton.setBorderPainted(false);
            salesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            salesButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    salesButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    salesButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    salesButtonMousePressed(e);
                }
            });

            //---- resupplyButton ----
            resupplyButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            resupplyButton.setForeground(new Color(0x6c39c1));
            resupplyButton.setBackground(new Color(0x6c39c1));
            resupplyButton.setBorder(null);
            resupplyButton.setHorizontalAlignment(SwingConstants.LEFT);
            resupplyButton.setFocusable(false);
            resupplyButton.setBorderPainted(false);
            resupplyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            resupplyButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    resupplyButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    resupplyButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    resupplyButtonMousePressed(e);
                }
            });
            resupplyButton.addActionListener(e -> resupply(e));

            //---- exitButton ----
            exitButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            exitButton.setForeground(new Color(0x6c39c1));
            exitButton.setBackground(new Color(0x6c39c1));
            exitButton.setBorder(null);
            exitButton.setHorizontalAlignment(SwingConstants.LEFT);
            exitButton.setFocusable(false);
            exitButton.setBorderPainted(false);
            exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    exitButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    exitButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    exitButtonMousePressed(e);
                }
            });
            exitButton.addActionListener(e -> exit(e));

            //---- financialsButton ----
            financialsButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            financialsButton.setForeground(new Color(0x6c39c1));
            financialsButton.setBackground(new Color(0x6c39c1));
            financialsButton.setBorder(null);
            financialsButton.setHorizontalAlignment(SwingConstants.LEFT);
            financialsButton.setFocusable(false);
            financialsButton.setBorderPainted(false);
            financialsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            financialsButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    financialsButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    financialsButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    financialsButtonMousePressed(e);
                }
            });
            financialsButton.addActionListener(e -> financials(e));

            GroupLayout sidePanelLayout = new GroupLayout(sidePanel);
            sidePanel.setLayout(sidePanelLayout);
            sidePanelLayout.setHorizontalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addContainerGap(19, Short.MAX_VALUE)
                        .addGroup(sidePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(financialsButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addGroup(sidePanelLayout.createParallelGroup()
                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                .addGroup(sidePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(appNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(appNameSubLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inventoryButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                                    .addComponent(dashboardButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                                .addComponent(salesButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                .addComponent(resupplyButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(19, Short.MAX_VALUE))
            );
            sidePanelLayout.setVerticalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(appNameLabel)
                        .addGap(0, 0, 0)
                        .addComponent(appNameSubLabel)
                        .addGap(44, 44, 44)
                        .addComponent(dashboardButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inventoryButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(salesButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resupplyButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(financialsButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 366, Short.MAX_VALUE)
                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Sales");
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
                        .addContainerGap(1056, Short.MAX_VALUE))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        //======== scrollPane1 ========
        {
            scrollPane1.setBorder(null);
            scrollPane1.setBackground(new Color(0xfcf8ff));
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //---- transactionHistoryTable ----
            transactionHistoryTable.setRowHeight(40);
            transactionHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            transactionHistoryTable.setFocusable(false);
            scrollPane1.setViewportView(transactionHistoryTable);
        }

        //======== controlsPanel ========
        {
            controlsPanel.setBackground(new Color(0xfcf8ff));

            //---- transactionHistoryLabel ----
            transactionHistoryLabel.setText("Transaction History");
            transactionHistoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            transactionHistoryLabel.setBackground(new Color(0xfcf8ff));
            transactionHistoryLabel.setForeground(new Color(0x251779));
            transactionHistoryLabel.setBorder(null);
            transactionHistoryLabel.setFocusable(false);
            transactionHistoryLabel.setEditable(false);

            //---- createOrderButton ----
            createOrderButton.setText("Create Order");
            createOrderButton.setFocusable(false);
            createOrderButton.setBackground(new Color(0x6c39c1));
            createOrderButton.setForeground(Color.white);
            createOrderButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            createOrderButton.addActionListener(e -> createOrder(e));

            //---- searchField ----
            searchField.setBorder(LineBorder.createBlackLineBorder());
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- viewDetailsButton ----
            viewDetailsButton.setText("View Details");
            viewDetailsButton.setFocusable(false);
            viewDetailsButton.setBackground(new Color(0x6c39c1));
            viewDetailsButton.setForeground(Color.white);
            viewDetailsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            viewDetailsButton.addActionListener(e -> viewDetails(e));

            GroupLayout controlsPanelLayout = new GroupLayout(controlsPanel);
            controlsPanel.setLayout(controlsPanelLayout);
            controlsPanelLayout.setHorizontalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(transactionHistoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 256, Short.MAX_VALUE)
                        .addComponent(viewDetailsButton, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createOrderButton, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
            );
            controlsPanelLayout.setVerticalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGroup(controlsPanelLayout.createParallelGroup()
                            .addGroup(controlsPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(transactionHistoryLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(controlsPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(viewDetailsButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(createOrderButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(10, Short.MAX_VALUE))
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
                        .addComponent(controlsPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE)
                            .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(controlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addGap(20, 20, 20))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JLabel appNameLabel;
    private JLabel appNameSubLabel;
    private JButton dashboardButton;
    private JButton inventoryButton;
    private JButton salesButton;
    private JButton resupplyButton;
    private JButton exitButton;
    private JButton financialsButton;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JScrollPane scrollPane1;
    private JTable transactionHistoryTable;
    private JPanel controlsPanel;
    private JTextField transactionHistoryLabel;
    private JButton createOrderButton;
    private JTextField searchField;
    private JButton viewDetailsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    //
    // Set Table Theme/Layout
    //
    private void setTableTheme() {
        // Use Custom Theme for Sales Table
        transactionHistoryTable.setShowGrid(false);
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
                    label.setHorizontalAlignment(SwingConstants.CENTER); // Center first column data
                }

                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < transactionHistoryTable.getColumnCount(); i++) {
            transactionHistoryTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Set Table Header Style
        JTableHeader header = transactionHistoryTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder()); // No borders
                label.setBackground(Color.decode("#6c39c1")); // Change background
                label.setForeground(Color.WHITE); // Change foreground
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center text
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return label;
            }
        });

        // Adjust column widths
        TableColumn transactionIdColumn = transactionHistoryTable.getColumnModel().getColumn(0);
        transactionIdColumn.setPreferredWidth(150);
        transactionIdColumn.setMinWidth(150);

        TableColumn dateColumn = transactionHistoryTable.getColumnModel().getColumn(1);
        dateColumn.setPreferredWidth(200);
        dateColumn.setMinWidth(200);

        TableColumn customerNameColumn = transactionHistoryTable.getColumnModel().getColumn(2);
        customerNameColumn.setPreferredWidth(300);
        customerNameColumn.setMinWidth(300);

        TableColumn totalPriceColumn = transactionHistoryTable.getColumnModel().getColumn(3);
        totalPriceColumn.setPreferredWidth(150);
        totalPriceColumn.setMinWidth(150);

        // Lock Column Re-order
        transactionHistoryTable.getTableHeader().setReorderingAllowed(false);
    }

    //
    // SQL Functionalities Section
    //
    void populateTableSales() {
        populateTableSales("");
    }

    void populateTableSales(String searchQuery) {
        String sql = "SELECT transaction_id, date, customer_name, total_price " +
                "FROM transaction_history " +
                "WHERE transaction_id LIKE ? OR " +
                "customer_name LIKE ? " +
                "ORDER BY date DESC";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String wildcardQuery = "%" + searchQuery + "%";
            pst.setString(1, wildcardQuery);
            pst.setString(2, wildcardQuery);

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make all cells non-editable
                }
            };

            model.setColumnIdentifiers(new String[]{"Transaction ID", "Date", "Customer Name", "Total Price"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("transaction_id"),
                        rs.getTimestamp("date"),
                        rs.getString("customer_name"),
                        String.format("%.2f", rs.getDouble("total_price")) // Format price with peso sign
                });
            }

            transactionHistoryTable.setModel(model);
            setTableTheme();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading transaction history: " + ex.getMessage());
        }
    }

    // Search Query
    private java.util.Timer searchTimer;
    private static final int SEARCH_DELAY = 300; // milliseconds

    private void searchListenerHandler() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchDatabase();
            }

            public void removeUpdate(DocumentEvent e) {
                searchDatabase();
            }

            public void changedUpdate(DocumentEvent e) {
                searchDatabase();
            }

            private void searchDatabase() {
                if (searchTimer != null) {
                    searchTimer.cancel(); // Cancel the previous timer
                }

                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            String query = searchField.getText().trim();
                            populateTableSales(query);
                        });
                    }
                }, SEARCH_DELAY);
            }
        });
    }

}
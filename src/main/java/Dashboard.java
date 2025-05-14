// Dashboard.java

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class Dashboard extends JPanel {
    public Dashboard() {
        // Use Custom Background Images for Side Panel Buttons
        //---- dashboardButton ----
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButtonActive.png")).getImage();
        dashboardButton = new ImageButton(dashboardBg, "");

        //---- inventoryButton ----
        Image inventoryBg = new ImageIcon(getClass().getResource("/assets/images/inventoryButton.png")).getImage();
        inventoryButton = new ImageButton(inventoryBg, "");

        //---- salesButton ----
        Image salesBg = new ImageIcon(getClass().getResource("/assets/images/salesButton.png")).getImage();
        salesButton = new ImageButton(salesBg, "");

        //---- financialsButton ----
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButton.png")).getImage();
        financialsButton = new ImageButton(financialsBg, "");

        //---- resupplyButton ----
        Image resupplyBg = new ImageIcon(getClass().getResource("/assets/images/resupplyButton.png")).getImage();
        resupplyButton = new ImageButton(resupplyBg, "");

        //---- exitButton ----
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/exitButton.png")).getImage();
        exitButton = new ImageButton(exitBg, "");

        initComponents();

        String[] columns = {"Date", "Item Name", "Quantity", "Unit Price", "Total Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        transactionHistoryTable.setModel(tableModel);

        setTableTheme();

        populateTableRecentSales();
    }

    //
    // Exit Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void exitButtonMouseEntered(MouseEvent e) {
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/exitButtonActive.png")).getImage();
        ((ImageButton) exitButton).setBackgroundImage(exitBg);
    }
    // Hover Effects - Mouse Exit
    private void exitButtonMouseExited(MouseEvent e) {
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/exitButton.png")).getImage();
        ((ImageButton) exitButton).setBackgroundImage(exitBg);
    }
    // Hover Effects - Mouse Press
    private void exitButtonMousePressed(MouseEvent e) {
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/exitButtonPressed.png")).getImage();
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
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButtonActive.png")).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Hover Effects - Mouse Exit
    private void dashboardButtonMouseExited(MouseEvent e) {
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButtonActive.png")).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Hover Effects - Mouse Press
    private void dashboardButtonMousePressed(MouseEvent e) {
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButtonActive.png")).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }

    //
    // Inventory Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void inventoryButtonMouseEntered(MouseEvent e) {
        Image inventoryBg = new ImageIcon(getClass().getResource("/assets/images/inventoryButtonActive.png")).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Hover Effects - Mouse Exit
    private void inventoryButtonMouseExited(MouseEvent e) {
        Image inventoryBg = new ImageIcon(getClass().getResource("/assets/images/inventoryButton.png")).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Hover Effects - Mouse Press
    private void inventoryButtonMousePressed(MouseEvent e) {
        Image inventoryBg = new ImageIcon(getClass().getResource("/assets/images/inventoryButtonPressed.png")).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Action Listener Method
    private void inventory(ActionEvent e) {
        // Open Inventory
        SwingUtilities.getWindowAncestor(this).dispose();; // Close Dashboard

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
        Image salesBg = new ImageIcon(getClass().getResource("/assets/images/salesButtonActive.png")).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Hover Effects - Mouse Exit
    private void salesButtonMouseExited(MouseEvent e) {
        Image salesBg = new ImageIcon(getClass().getResource("/assets/images/salesButton.png")).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Hover Effects - Mouse Press
    private void salesButtonMousePressed(MouseEvent e) {
        Image salesBg = new ImageIcon(getClass().getResource("/assets/images/salesButtonPressed.png")).getImage();
        ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Action Listener Method
    private void sales(ActionEvent e) {
        // Open Sales
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

        JFrame frame = new JFrame("Sales");
        frame.setContentPane(new Sales());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
        // TODO
    }

    //
    // Resupply Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void resupplyButtonMouseEntered(MouseEvent e) {
        Image resupplyBg = new ImageIcon(getClass().getResource("/assets/images/resupplyButtonActive.png")).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Hover Effects - Mouse Exit
    private void resupplyButtonMouseExited(MouseEvent e) {
        Image resupplyBg = new ImageIcon(getClass().getResource("/assets/images/resupplyButton.png")).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Hover Effects - Mouse Press
    private void resupplyButtonMousePressed(MouseEvent e) {
        Image resupplyBg = new ImageIcon(getClass().getResource("/assets/images/resupplyButtonPressed.png")).getImage();
        ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Action Listener Method
    private void resupply(ActionEvent e) {
        // Open Resupply
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

        JFrame frame = new JFrame("Resupply");
        frame.setContentPane(new Resupply());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        appNameLabel = new JLabel();
        appNameSubLabel = new JLabel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        dashboardPanel1 = new JPanel();
        dashboardPanel1Container1 = new JPanel();
        totalSalesPanel = new JPanel();
        dashboardPanel1Container2 = new JPanel();
        totalExpensesPanel = new JPanel();
        dashboardPanel1Container3 = new JPanel();
        totalOrdersPanel = new JPanel();
        dashboardPanel1Container4 = new JPanel();
        totalProductsPanel = new JPanel();
        salesExpensesPanel = new JPanel();
        dashboardLabel2 = new JTextField();
        scrollPane1 = new JScrollPane();
        transactionHistoryTable = new JTable();
        stockAlertPanel = new JPanel();

        //======== this ========
        setBackground(new Color(0xe8e7f4));

        //======== sidePanel ========
        {
            sidePanel.setBackground(new Color(0x6c39c1));
            sidePanel.setMaximumSize(new Dimension(260, 32823));
            sidePanel.setMinimumSize(new Dimension(260, 62));
            sidePanel.setPreferredSize(new Dimension(260, 820));

            //---- appNameLabel ----
            appNameLabel.setText("SCAPE Project");
            appNameLabel.setFont(new Font("Segoe UI Black", Font.ITALIC, 30));
            appNameLabel.setForeground(Color.white);
            appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            //---- appNameSubLabel ----
            appNameSubLabel.setText("by group 2");
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
            salesButton.addActionListener(e -> sales(e));

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

            GroupLayout sidePanelLayout = new GroupLayout(sidePanel);
            sidePanel.setLayout(sidePanelLayout);
            sidePanelLayout.setHorizontalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addContainerGap(19, Short.MAX_VALUE)
                        .addGroup(sidePanelLayout.createParallelGroup()
                            .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addGroup(sidePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(appNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(appNameSubLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inventoryButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                                .addComponent(dashboardButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                            .addComponent(salesButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addComponent(financialsButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addComponent(resupplyButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(financialsButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resupplyButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 366, Short.MAX_VALUE)
                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Dashboard Overview");
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
                        .addContainerGap(942, Short.MAX_VALUE))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        //======== dashboardPanel1 ========
        {
            dashboardPanel1.setBackground(new Color(0xfcf8ff));
            dashboardPanel1.setFocusable(false);
            dashboardPanel1.setLayout(new BoxLayout(dashboardPanel1, BoxLayout.X_AXIS));

            //======== dashboardPanel1Container1 ========
            {
                dashboardPanel1Container1.setPreferredSize(new Dimension(274, 238));
                dashboardPanel1Container1.setBackground(new Color(0xfcf8ff));

                //======== totalSalesPanel ========
                {
                    totalSalesPanel.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalSalesPanelLayout = new GroupLayout(totalSalesPanel);
                    totalSalesPanel.setLayout(totalSalesPanelLayout);
                    totalSalesPanelLayout.setHorizontalGroup(
                        totalSalesPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    totalSalesPanelLayout.setVerticalGroup(
                        totalSalesPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                }

                GroupLayout dashboardPanel1Container1Layout = new GroupLayout(dashboardPanel1Container1);
                dashboardPanel1Container1.setLayout(dashboardPanel1Container1Layout);
                dashboardPanel1Container1Layout.setHorizontalGroup(
                    dashboardPanel1Container1Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container1Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(totalSalesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(10, 10, 10))
                );
                dashboardPanel1Container1Layout.setVerticalGroup(
                    dashboardPanel1Container1Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, dashboardPanel1Container1Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(totalSalesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))
                );
            }
            dashboardPanel1.add(dashboardPanel1Container1);

            //======== dashboardPanel1Container2 ========
            {
                dashboardPanel1Container2.setBackground(new Color(0xfcf8ff));
                dashboardPanel1Container2.setPreferredSize(new Dimension(274, 238));

                //======== totalExpensesPanel ========
                {
                    totalExpensesPanel.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalExpensesPanelLayout = new GroupLayout(totalExpensesPanel);
                    totalExpensesPanel.setLayout(totalExpensesPanelLayout);
                    totalExpensesPanelLayout.setHorizontalGroup(
                        totalExpensesPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    totalExpensesPanelLayout.setVerticalGroup(
                        totalExpensesPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                }

                GroupLayout dashboardPanel1Container2Layout = new GroupLayout(dashboardPanel1Container2);
                dashboardPanel1Container2.setLayout(dashboardPanel1Container2Layout);
                dashboardPanel1Container2Layout.setHorizontalGroup(
                    dashboardPanel1Container2Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container2Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(totalExpensesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(10, 10, 10))
                );
                dashboardPanel1Container2Layout.setVerticalGroup(
                    dashboardPanel1Container2Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container2Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(totalExpensesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))
                );
            }
            dashboardPanel1.add(dashboardPanel1Container2);

            //======== dashboardPanel1Container3 ========
            {
                dashboardPanel1Container3.setPreferredSize(new Dimension(274, 238));
                dashboardPanel1Container3.setBackground(new Color(0xfcf8ff));

                //======== totalOrdersPanel ========
                {
                    totalOrdersPanel.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalOrdersPanelLayout = new GroupLayout(totalOrdersPanel);
                    totalOrdersPanel.setLayout(totalOrdersPanelLayout);
                    totalOrdersPanelLayout.setHorizontalGroup(
                        totalOrdersPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    totalOrdersPanelLayout.setVerticalGroup(
                        totalOrdersPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                }

                GroupLayout dashboardPanel1Container3Layout = new GroupLayout(dashboardPanel1Container3);
                dashboardPanel1Container3.setLayout(dashboardPanel1Container3Layout);
                dashboardPanel1Container3Layout.setHorizontalGroup(
                    dashboardPanel1Container3Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container3Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(totalOrdersPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(10, 10, 10))
                );
                dashboardPanel1Container3Layout.setVerticalGroup(
                    dashboardPanel1Container3Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container3Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(totalOrdersPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))
                );
            }
            dashboardPanel1.add(dashboardPanel1Container3);

            //======== dashboardPanel1Container4 ========
            {
                dashboardPanel1Container4.setBackground(new Color(0xfcf8ff));
                dashboardPanel1Container4.setPreferredSize(new Dimension(274, 238));

                //======== totalProductsPanel ========
                {
                    totalProductsPanel.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalProductsPanelLayout = new GroupLayout(totalProductsPanel);
                    totalProductsPanel.setLayout(totalProductsPanelLayout);
                    totalProductsPanelLayout.setHorizontalGroup(
                        totalProductsPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                    totalProductsPanelLayout.setVerticalGroup(
                        totalProductsPanelLayout.createParallelGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                    );
                }

                GroupLayout dashboardPanel1Container4Layout = new GroupLayout(dashboardPanel1Container4);
                dashboardPanel1Container4.setLayout(dashboardPanel1Container4Layout);
                dashboardPanel1Container4Layout.setHorizontalGroup(
                    dashboardPanel1Container4Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container4Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(totalProductsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))
                );
                dashboardPanel1Container4Layout.setVerticalGroup(
                    dashboardPanel1Container4Layout.createParallelGroup()
                        .addGroup(dashboardPanel1Container4Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(totalProductsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))
                );
            }
            dashboardPanel1.add(dashboardPanel1Container4);
        }

        //======== salesExpensesPanel ========
        {
            salesExpensesPanel.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel2 ----
            dashboardLabel2.setText("Most Recent Transactions");
            dashboardLabel2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dashboardLabel2.setBackground(new Color(0xfcf8ff));
            dashboardLabel2.setForeground(new Color(0x251779));
            dashboardLabel2.setBorder(null);
            dashboardLabel2.setFocusable(false);
            dashboardLabel2.setEditable(false);

            //======== scrollPane1 ========
            {

                //---- transactionHistoryTable ----
                transactionHistoryTable.setRowHeight(40);
                transactionHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                transactionHistoryTable.setFocusable(false);
                scrollPane1.setViewportView(transactionHistoryTable);
            }

            GroupLayout salesExpensesPanelLayout = new GroupLayout(salesExpensesPanel);
            salesExpensesPanel.setLayout(salesExpensesPanelLayout);
            salesExpensesPanelLayout.setHorizontalGroup(
                salesExpensesPanelLayout.createParallelGroup()
                    .addGroup(salesExpensesPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(salesExpensesPanelLayout.createParallelGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
                            .addGroup(salesExpensesPanelLayout.createSequentialGroup()
                                .addComponent(dashboardLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 473, Short.MAX_VALUE)))
                        .addGap(20, 20, 20))
            );
            salesExpensesPanelLayout.setVerticalGroup(
                salesExpensesPanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, salesExpensesPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
            );
        }

        //======== stockAlertPanel ========
        {
            stockAlertPanel.setBackground(new Color(0xfcf8ff));

            GroupLayout stockAlertPanelLayout = new GroupLayout(stockAlertPanel);
            stockAlertPanel.setLayout(stockAlertPanelLayout);
            stockAlertPanelLayout.setHorizontalGroup(
                stockAlertPanelLayout.createParallelGroup()
                    .addGap(0, 350, Short.MAX_VALUE)
            );
            stockAlertPanelLayout.setVerticalGroup(
                stockAlertPanelLayout.createParallelGroup()
                    .addGap(0, 447, Short.MAX_VALUE)
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
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(dashboardPanel1, GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(salesExpensesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(24, 24, 24)
                                    .addComponent(stockAlertPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(dashboardPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(stockAlertPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(salesExpensesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private JButton financialsButton;
    private JButton resupplyButton;
    private JButton exitButton;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JPanel dashboardPanel1;
    private JPanel dashboardPanel1Container1;
    private JPanel totalSalesPanel;
    private JPanel dashboardPanel1Container2;
    private JPanel totalExpensesPanel;
    private JPanel dashboardPanel1Container3;
    private JPanel totalOrdersPanel;
    private JPanel dashboardPanel1Container4;
    private JPanel totalProductsPanel;
    private JPanel salesExpensesPanel;
    private JTextField dashboardLabel2;
    private JScrollPane scrollPane1;
    private JTable transactionHistoryTable;
    private JPanel stockAlertPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void setTableTheme() {
        // Use Custom Theme for Recent Sales Table
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

        // Set Table Column Size
        // First Column
        TableColumn firstColumn = transactionHistoryTable.getColumnModel().getColumn(0);
        firstColumn.setPreferredWidth(70);
        firstColumn.setMinWidth(70);
        // Second Column
        TableColumn secondColumn = transactionHistoryTable.getColumnModel().getColumn(1);
        secondColumn.setPreferredWidth(100);
        secondColumn.setMinWidth(100);
        // Third Column
        TableColumn thirdColumn = transactionHistoryTable.getColumnModel().getColumn(2);
        thirdColumn.setPreferredWidth(60);
        thirdColumn.setMinWidth(60);
        // Fourth Column
        TableColumn fourthColumn = transactionHistoryTable.getColumnModel().getColumn(3);
        fourthColumn.setPreferredWidth(60);
        fourthColumn.setMinWidth(60);
        // Fifth Column
        TableColumn fifthColumn = transactionHistoryTable.getColumnModel().getColumn(4);
        fifthColumn.setPreferredWidth(60);
        fifthColumn.setMinWidth(60);

        // Lock Column Re-order
        transactionHistoryTable.getTableHeader().setReorderingAllowed(false);
    }

    //
    // SQL Functionalities Section
    //
    void populateTableRecentSales() {
        String sql = "SELECT date, item_name, quantity, price, total_price " +
                "FROM transaction_history " +
                "ORDER BY date";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Date", "Item Name", "Quantity", "Unit Price", "Total Price"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getTimestamp("date"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total_price")
                });
            }

            transactionHistoryTable.setModel(model);
            setTableTheme();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading transaction history: " + ex.getMessage());
        }
    }
}

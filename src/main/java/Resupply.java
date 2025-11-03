// Resupply.java

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Resupply extends JPanel {
    public Resupply() {
        // Use Custom Background Images for Side Panel Buttons
        //---- dashboardButton ----
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButton.png"))).getImage();
        dashboardButton = new ImageButton(dashboardBg, "");

        //---- inventoryButton ----
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButton.png"))).getImage();
        inventoryButton = new ImageButton(inventoryBg, "");

        //---- salesButton ----
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButton.png"))).getImage();
        salesButton = new ImageButton(salesBg, "");

        //---- financialsButton ----
        Image financialsBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/financialsButton.png"))).getImage();
        financialsButton = new ImageButton(financialsBg, "");

        //---- resupplyButton ----
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButtonActive.png"))).getImage();
        resupplyButton = new ImageButton(resupplyBg, "");

        //---- exitButton ----
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButton.png"))).getImage();
        exitButton = new ImageButton(exitBg, "");

        // Use Custom Background Images for Resupply Buttons
        //---- editItemButton ----
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyItemButton.png"))).getImage();
        editButton = new ImageButton(editItemBg, "");

        //---- refreshButton ----
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButton.png"))).getImage();
        refreshButton = new ImageButton(refreshBg, "");

        initComponents();
        populateTable(); // Refresh the table
        populateResupplyHistory(); // Refresh the resupply history table
        searchListenerHandler(); // Search Listener Handler for searchField

        // Make table rows non-selectable
        resupplyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resupplyTable.setRowSelectionAllowed(false);
        resupplyTable.setCellSelectionEnabled(false);
        resupplyTable.getTableHeader().setReorderingAllowed(false);
        resupplyTable.setFocusable(false);

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Resupply

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Resupply

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
            Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButton.png"))).getImage();
            ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Hover Effects - Mouse Press
    private void salesButtonMousePressed(MouseEvent e) {
            Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButtonPressed.png"))).getImage();
            ((ImageButton) salesButton).setBackgroundImage(salesBg);
    }
    // Action Listener Method
    private void sales(ActionEvent e) {
        // Open Sales
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Resupply

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
        // Open Financials
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Resupply

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
            Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButtonActive.png"))).getImage();
            ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }
    // Hover Effects - Mouse Press
    private void resupplyButtonMousePressed(MouseEvent e) {
            Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButtonActive.png"))).getImage();
            ((ImageButton) resupplyButton).setBackgroundImage(resupplyBg);
    }

    //
    // Edit Item Button Event Listener Methods
    //
    // Action Listener Method
    private void edit(ActionEvent e) {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemId = inventoryTable.getValueAt(selectedRow, 1).toString();

            JFrame frame = new JFrame("Resupply for Item " + itemId);
            frame.setContentPane(new ResupplyItemForm(itemId, this));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);          // Disable window resizing
            frame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select an item to resupply");
        }
    }

    // Hover Effects - Mouse Enter
    private void editButtonMouseEntered(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyItemButtonActive.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }
    // Hover Effects - Mouse Exit
    private void editButtonMouseExited(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyItemButton.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }
    // Hover Effects - Mouse Press
    private void editButtonMousePressed(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyItemButtonPressed.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }

    //
    // Refresh Button Event Listener Methods
    //
    // Action Listener Method
    private void refresh(ActionEvent e) {
        String query = searchField.getText().trim();
        populateTable(query);
        populateResupplyHistory();
    }
    // Hover Effects - Mouse Enter
    private void refreshButtonMouseEntered(MouseEvent e) {
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButtonActive.png"))).getImage();
        ((ImageButton) refreshButton).setBackgroundImage(refreshBg);
    }
    // Hover Effects - Mouse Exit
    private void refreshButtonMouseExited(MouseEvent e) {
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButton.png"))).getImage();
        ((ImageButton) refreshButton).setBackgroundImage(refreshBg);
    }
    // Hover Effects - Mouse Press
    private void refreshButtonMousePressed(MouseEvent e) {
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButtonPressed.png"))).getImage();
        ((ImageButton) refreshButton).setBackgroundImage(refreshBg);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        appNameLabel = new JLabel();
        appNameSubLabel = new JLabel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        controlsPanel = new JPanel();
        searchField = new JTextField();
        scrollPane1 = new JScrollPane();
        inventoryTable = new JTable();
        scrollPane2 = new JScrollPane();
        resupplyTable = new JTable();
        resupplyHistoryTableLabel = new JTextField();

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
            dashboardButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            dashboardLabel.setText("Resupply");
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

        //======== controlsPanel ========
        {
            controlsPanel.setBackground(new Color(0xfcf8ff));

            //---- searchField ----
            searchField.setBorder(LineBorder.createBlackLineBorder());
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- refreshButton ----
            refreshButton.setFocusable(false);
            refreshButton.addActionListener(e -> refresh(e));
            refreshButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    refreshButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    refreshButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    refreshButtonMousePressed(e);
                }
            });

            //---- editButton ----
            editButton.setFocusable(false);
            editButton.addActionListener(e -> edit(e));
            editButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    editButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    editButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    editButtonMousePressed(e);
                }
            });

            GroupLayout controlsPanelLayout = new GroupLayout(controlsPanel);
            controlsPanel.setLayout(controlsPanelLayout);
            controlsPanelLayout.setHorizontalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 557, Short.MAX_VALUE)
                        .addComponent(editButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
            controlsPanelLayout.setVerticalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(controlsPanelLayout.createParallelGroup()
                            .addComponent(refreshButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editButton, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                        .addGap(10, 10, 10))
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))
            );
        }

        //======== scrollPane1 ========
        {
            scrollPane1.setBorder(null);
            scrollPane1.setBackground(new Color(0xfcf8ff));
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //---- inventoryTable ----
            inventoryTable.setRowHeight(40);
            inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            scrollPane1.setViewportView(inventoryTable);
        }

        //======== scrollPane2 ========
        {
            scrollPane2.setBorder(null);
            scrollPane2.setBackground(new Color(0xfcf8ff));
            scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //---- resupplyTable ----
            resupplyTable.setRowHeight(40);
            resupplyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            scrollPane2.setViewportView(resupplyTable);
        }

        //---- resupplyHistoryTableLabel ----
        resupplyHistoryTableLabel.setText("Resupply History");
        resupplyHistoryTableLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        resupplyHistoryTableLabel.setBackground(new Color(0xe8e7f4));
        resupplyHistoryTableLabel.setForeground(new Color(0x251779));
        resupplyHistoryTableLabel.setBorder(null);
        resupplyHistoryTableLabel.setFocusable(false);
        resupplyHistoryTableLabel.setEditable(false);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(sidePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(windowTitleContainer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(controlsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(resupplyHistoryTableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 1116, Short.MAX_VALUE)
                                .addComponent(scrollPane2))
                            .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(controlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(resupplyHistoryTableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
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
    private JPanel controlsPanel;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton editButton;
    private JScrollPane scrollPane1;
    private JTable inventoryTable;
    private JScrollPane scrollPane2;
    private JTable resupplyTable;
    private JTextField resupplyHistoryTableLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    //
    // Set Table Theme/Layout
    //
    private void setTableTheme() {
        // Use Custom Theme for Inventory Table
        inventoryTable.setShowGrid(false);
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
        for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
            inventoryTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Set Table Header Style
        JTableHeader header = inventoryTable.getTableHeader();
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
        TableColumn firstColumn = inventoryTable.getColumnModel().getColumn(0);
        firstColumn.setPreferredWidth(35);
        firstColumn.setMinWidth(35);
        firstColumn.setMaxWidth(35);
        firstColumn.setResizable(false);
        // Second Column
        TableColumn secondColumn = inventoryTable.getColumnModel().getColumn(1);
        secondColumn.setPreferredWidth(60);
        secondColumn.setMinWidth(60);
        // Third Column
        TableColumn thirdColumn = inventoryTable.getColumnModel().getColumn(2);
        thirdColumn.setPreferredWidth(215);
        thirdColumn.setMinWidth(215);
        // Fourth Column
        TableColumn fourthColumn = inventoryTable.getColumnModel().getColumn(3);
        fourthColumn.setPreferredWidth(50);
        fourthColumn.setMinWidth(50);
        // Fifth Column
        TableColumn fifthColumn = inventoryTable.getColumnModel().getColumn(4);
        fifthColumn.setPreferredWidth(50);
        fifthColumn.setMinWidth(50);
        // Sixth Column
        TableColumn sixthColumn = inventoryTable.getColumnModel().getColumn(5);
        sixthColumn.setPreferredWidth(50);
        sixthColumn.setMinWidth(50);

        // Lock Column Re-order
        inventoryTable.getTableHeader().setReorderingAllowed(false);
    }

    // Style the resupply history table
    private void setResupplyTableTheme() {
        resupplyTable.setShowGrid(false);
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

                // Apply left padding
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                label.setHorizontalAlignment(SwingConstants.LEFT);

                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < resupplyTable.getColumnCount(); i++) {
            resupplyTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Set Table Header Style
        JTableHeader header = resupplyTable.getTableHeader();
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

        // Lock Column Re-order
        resupplyTable.getTableHeader().setReorderingAllowed(false);
    }

    //
    // SQL Functionalities Section
    //
    // Refresh/Populate Table
    void populateTable() {
        populateTable("");
    }

    void populateTable(String searchQuery) {
        String sql = "SELECT item_no, item_id, item_name, category, quantity, price FROM inventory " +
                "WHERE (item_id LIKE ? OR item_name LIKE ? OR category LIKE ?)" +
                "AND quantity < 20 " +
                "ORDER BY quantity"; // Only fetch low stock items (less than 20)

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)){
            String wildcardQuery = "%" + searchQuery + "%";
            pst.setString(1, wildcardQuery);
            pst.setString(2, wildcardQuery);
            pst.setString(3, wildcardQuery);

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make all cells non-editable
                }
            };

            model.setColumnIdentifiers(new String[]{"#", "Item ID", "Item Name", "Category", "Quantity", "Price"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("item_no"),
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                });
            }

            inventoryTable.setModel(model);
            setTableTheme();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage());
        }
    }

    void populateResupplyHistory() {
        String sql = "SELECT resupply_id, item_id, item_name, quantity, supplier_name, unit_cost, total_cost, resupply_date " +
                "FROM resupply_history ORDER BY resupply_date DESC";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make all cells non-editable
                }
            };

            model.setColumnIdentifiers(new String[]{"ID", "Item ID", "Item Name", "Quantity", "Supplier", "Unit Cost", "Total Cost", "Date"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("resupply_id"),
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("supplier_name"),
                        rs.getDouble("unit_cost"),
                        rs.getDouble("total_cost"),
                        rs.getTimestamp("resupply_date")
                });
            }

            resupplyTable.setModel(model);
            setResupplyTableTheme();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading resupply history: " + ex.getMessage());
        }
    }

    // Search Query
    private Timer searchTimer;
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
                            populateTable(query);
                        });
                    }
                }, SEARCH_DELAY);
            }
        });
    }
}
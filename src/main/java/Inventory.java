// Inventory.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
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

public class Inventory extends JPanel {
    public Inventory() {
        // Use Custom Background Images for Side Panel Buttons
        //---- dashboardButton ----
        Image dashboardBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/dashboardButton.png"))).getImage();
        dashboardButton = new ImageButton(dashboardBg, "");

        //---- inventoryButton ----
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButtonActive.png"))).getImage();
        inventoryButton = new ImageButton(inventoryBg, "");

        //---- salesButton ----
        Image salesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/salesButton.png"))).getImage();
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

        // Use Custom Background Images for Inventory Buttons
        //---- addItemButton ----
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButton.png"))).getImage();
        addButton = new ImageButton(addItemBg, "");

        //---- deleteItemButton ----
        Image deleteItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/deleteItemButton.png"))).getImage();
        deleteButton = new ImageButton(deleteItemBg, "");

        //---- editItemButton ----
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/editItemButton.png"))).getImage();
        editButton = new ImageButton(editItemBg, "");

        //---- refreshButton ----
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButton.png"))).getImage();
        refreshButton = new ImageButton(refreshBg, "");

        initComponents();
        populateTable(); // Refresh the table
        searchListenerHandler(); // Search Listener Handler for searchField

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Inventory

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
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButtonActive.png"))).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
    }
    // Hover Effects - Mouse Press
    private void inventoryButtonMousePressed(MouseEvent e) {
        Image inventoryBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/inventoryButton.png"))).getImage();
        ((ImageButton) inventoryButton).setBackgroundImage(inventoryBg);
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Inventory

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
        Image financialsBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/financialsButtonActive.png"))).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Hover Effects - Mouse Exit
    private void financialsButtonMouseExited(MouseEvent e) {
        Image financialsBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/financialsButton.png"))).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Hover Effects - Mouse Press
    private void financialsButtonMousePressed(MouseEvent e) {
        Image financialsBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/financialsButtonPressed.png"))).getImage();
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

        JFrame frame = new JFrame("Resupply");
        frame.setContentPane(new Resupply());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Add Item Button Event Listener Methods
    //
    // Action Listener Method
    private void add(ActionEvent e) {
        JFrame frame = new JFrame("Add Item");
        frame.setContentPane(new AddItemForm(this));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);          // Disable window resizing
        frame.setVisible(true);
    }
    // Hover Effects - Mouse Enter
    private void addButtonMouseEntered(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButtonActive.png"))).getImage();
        ((ImageButton) addButton).setBackgroundImage(addItemBg);
    }
    // Hover Effects - Mouse Exit
    private void addButtonMouseExited(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButton.png"))).getImage();
        ((ImageButton) addButton).setBackgroundImage(addItemBg);
    }
    // Hover Effects - Mouse Press
    private void addButtonMousePressed(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButtonPressed.png"))).getImage();
        ((ImageButton) addButton).setBackgroundImage(addItemBg);
    }

    //
    // Delete Item Button Event Listener Methods
    //
    // Action Listener Method
    private void delete(ActionEvent e) {
        deleteSelectedItem();
    }
    // Hover Effects - Mouse Enter
    private void deleteButtonMouseEntered(MouseEvent e) {
        Image deleteItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/deleteItemButtonActive.png"))).getImage();
        ((ImageButton) deleteButton).setBackgroundImage(deleteItemBg);
    }
    // Hover Effects - Mouse Exit
    private void deleteButtonMouseExited(MouseEvent e) {
        Image deleteItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/deleteItemButton.png"))).getImage();
        ((ImageButton) deleteButton).setBackgroundImage(deleteItemBg);
    }
    // Hover Effects - Mouse Press
    private void deleteButtonMousePressed(MouseEvent e) {
        Image deleteItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/deleteItemButtonPressed.png"))).getImage();
        ((ImageButton) deleteButton).setBackgroundImage(deleteItemBg);
    }

    //
    // Edit Item Button Event Listener Methods
    //
    // Action Listener Method
    private void edit(ActionEvent e) {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemId = inventoryTable.getValueAt(selectedRow, 1).toString();

            JFrame frame = new JFrame("Editing Item " + itemId);
            frame.setContentPane(new EditItemForm(itemId, this));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);          // Disable window resizing
            frame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to edit.");
        }
    }
    // Hover Effects - Mouse Enter
    private void editButtonMouseEntered(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/editItemButtonActive.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }
    // Hover Effects - Mouse Exit
    private void editButtonMouseExited(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/editItemButton.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }
    // Hover Effects - Mouse Press
    private void editButtonMousePressed(MouseEvent e) {
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/editItemButtonPressed.png"))).getImage();
        ((ImageButton) editButton).setBackgroundImage(editItemBg);
    }

    //
    // Refresh Button Event Listener Methods
    //
    // Action Listener Method
    private void refresh(ActionEvent e) {
        String query = searchField.getText().trim();
        populateTable(query);
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
            dashboardLabel.setText("Inventory");
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
                        .addContainerGap(1036, Short.MAX_VALUE))
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

            //---- deleteButton ----
            deleteButton.setFocusable(false);
            deleteButton.addActionListener(e -> delete(e));
            deleteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    deleteButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    deleteButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    deleteButtonMousePressed(e);
                }
            });

            //---- addButton ----
            addButton.setFocusable(false);
            addButton.addActionListener(e -> add(e));
            addButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    addButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    addButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    addButtonMousePressed(e);
                }
            });

            GroupLayout controlsPanelLayout = new GroupLayout(controlsPanel);
            controlsPanel.setLayout(controlsPanelLayout);
            controlsPanelLayout.setHorizontalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 315, Short.MAX_VALUE)
                        .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
                            .addComponent(editButton, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                            .addComponent(addButton, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
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
                    .addGap(18, 18, 18)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
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
    private JPanel controlsPanel;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton addButton;
    private JScrollPane scrollPane1;
    private JTable inventoryTable;
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

    //
    // SQL Functionalities Section
    //
     // Refresh/Populate Table
    void populateTable() {
        populateTable("");
    }

    void populateTable(String searchQuery) {
        String sql = "SELECT item_no, item_id, item_name, category, quantity, price FROM inventory " +
                "WHERE item_id LIKE ? OR item_name LIKE ? OR category LIKE ? " +
                "ORDER BY item_no";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)){
            String wildcardQuery = "%" + searchQuery + "%";
            pst.setString(1, wildcardQuery);
            pst.setString(2, wildcardQuery);
            pst.setString(3, wildcardQuery);

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
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

    // Delete Item
    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the item_id from the selected row
        String itemId = inventoryTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM inventory WHERE item_id = ?";

            try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, itemId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Item deleted successfully.");
                    populateTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Item could not be deleted. Please try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
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

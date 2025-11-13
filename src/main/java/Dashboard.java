// Dashboard.java

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
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

        //---- userManagementButton ----
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButton.png")).getImage();
        userManagementButton = new ImageButton(userManagementBg, "");

        //---- discountCodesButton ----
        Image discountCodesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/discountCodesButton.png"))).getImage();
        discountCodesButton = new ImageButton(discountCodesBg, "");

        //---- exitButton ----
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/exitButton.png")).getImage();
        exitButton = new ImageButton(exitBg, "");

        //---- backupDatabaseButton ----
        Image backupDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/backupDatabaseButton.png")).getImage();
        backupDatabaseButton = new ImageButton(backupDatabaseBg, "");

        //---- restoreDatabaseButton ----
        Image restoreDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/importDatabaseButton.png")).getImage();
        restoreDatabaseButton = new ImageButton(restoreDatabaseBg, "");

        initComponents();

        // Set the current date in the date label
        setCurrentDate();

        String[] columns = {"Date", "Item Name", "Quantity", "Unit Price", "Total Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        transactionHistoryTable.setModel(tableModel);

        setTableTheme();

        setupLowStockTable();
        populateTableRecentSales();
        updateDashboardStatistics();

        // Make table rows non-selectable
        lowStockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lowStockTable.setRowSelectionAllowed(false);
        lowStockTable.setCellSelectionEnabled(false);
        lowStockTable.getTableHeader().setReorderingAllowed(false);
        lowStockTable.setFocusable(false);
        transactionHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionHistoryTable.setRowSelectionAllowed(false);
        transactionHistoryTable.setCellSelectionEnabled(false);
        transactionHistoryTable.getTableHeader().setReorderingAllowed(false);
        transactionHistoryTable.setFocusable(false);

        // Check user role and manage button visibility
        String userRole = UserSession.getRole();

        // Define button access for each role using arrays
        Map<String, JButton[]> restrictedButtons = new HashMap<>();
        restrictedButtons.put("MANAGER", new JButton[]{
                userManagementButton, backupDatabaseButton, restoreDatabaseButton});
        restrictedButtons.put("STOCK CLERK", new JButton[]{
                dashboardButton, userManagementButton, financialsButton,
                inventoryButton, salesButton, discountCodesButton,
                backupDatabaseButton, restoreDatabaseButton
        });

        // Get buttons to restrict based on role, default to admin-only buttons for non-admin roles
        JButton[] buttonsToRestrict = restrictedButtons.getOrDefault(userRole, new JButton[]{
                dashboardButton, userManagementButton, financialsButton, resupplyButton,
                discountCodesButton, backupDatabaseButton, restoreDatabaseButton
        });

        // Only apply restrictions if not an admin
        if (!"ADMIN".equals(userRole)) {
            for (JButton button : buttonsToRestrict) {
                button.setVisible(false);
                button.setEnabled(false);
            }
        }
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
        // Open Financials
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

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

    //
    // User Management Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void userManagementButtonMouseEntered(MouseEvent e) {
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButtonActive.png")).getImage();
        ((ImageButton) userManagementButton).setBackgroundImage(userManagementBg);
    }
    // Hover Effects - Mouse Exit
    private void userManagementButtonMouseExited(MouseEvent e) {
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButton.png")).getImage();
        ((ImageButton) userManagementButton).setBackgroundImage(userManagementBg);
    }
    // Hover Effects - Mouse Press
    private void userManagementButtonMousePressed(MouseEvent e) {
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButtonPressed.png")).getImage();
        ((ImageButton) userManagementButton).setBackgroundImage(userManagementBg);
    }
    // Action Listener Method
    private void userManagement(ActionEvent e) {
        // Open User Management
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

        JFrame frame = new JFrame("User Management");
        frame.setContentPane(new UserManagement());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Discount Codes Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void discountCodesButtonMouseEntered(MouseEvent e) {
        Image discountCodesBg = new ImageIcon(getClass().getResource("/assets/images/discountCodesButtonActive.png")).getImage();
        ((ImageButton) discountCodesButton).setBackgroundImage(discountCodesBg);
    }
    // Hover Effects - Mouse Exit
    private void discountCodesButtonMouseExited(MouseEvent e) {
        Image discountCodesBg = new ImageIcon(getClass().getResource("/assets/images/discountCodesButton.png")).getImage();
        ((ImageButton) discountCodesButton).setBackgroundImage(discountCodesBg);
    }
    // Hover Effects - Mouse Press
    private void discountCodesButtonMousePressed(MouseEvent e) {
        Image discountCodesBg = new ImageIcon(getClass().getResource("/assets/images/discountCodesButtonPressed.png")).getImage();
        ((ImageButton) discountCodesButton).setBackgroundImage(discountCodesBg);
    }
    // Action Listener Method
    private void discountCodes(ActionEvent e) {
        // Open Discount Codes
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Dashboard

        JFrame frame = new JFrame("Discount Codes");
        frame.setContentPane(new DiscountCodes());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //
    // Backup Database Button Event Listener Methods
    //
    // Action Listener Method
    private void backupDatabase(ActionEvent e) {
        try {
            String backupDir = DBConnection.getBackupDirectory();

            // Create file chooser dialog
            JFileChooser fileChooser = new JFileChooser(backupDir);
            fileChooser.setDialogTitle("Save Database Backup");

            // Set file extension filter
            FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL Files (*.sql)", "sql");
            fileChooser.setFileFilter(filter);

            // Set default file name with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            fileChooser.setSelectedFile(new File("backup_" + timestamp + ".sql"));

            // Show save dialog
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                // Append .sql extension if not present
                if (!file.getName().toLowerCase().endsWith(".sql")) {
                    file = new File(file.getAbsolutePath() + ".sql");
                }

                // Confirm overwrite if file exists
                if (file.exists()) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "File already exists. Do you want to overwrite it?",
                            "Confirm Overwrite",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    // Show wait cursor during backup
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    // Perform backup
                    DBConnection.backupDatabase(file.getAbsolutePath());

                    // Show success message
                    JOptionPane.showMessageDialog(this,
                            "Database backup completed successfully!",
                            "Backup Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error creating backup: " + ex.getMessage(),
                            "Backup Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error accessing backup directory: " + ex.getMessage(),
                    "Directory Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    // Hover Effects - Mouse Enter
    private void backupDatabaseButtonMouseEntered(MouseEvent e) {
        Image backupDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/backupDatabaseButtonActive.png")).getImage();
        ((ImageButton) backupDatabaseButton).setBackgroundImage(backupDatabaseBg);
    }
    // Hover Effects - Mouse Exit
    private void backupDatabaseButtonMouseExited(MouseEvent e) {
        Image backupDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/backupDatabaseButton.png")).getImage();
        ((ImageButton) backupDatabaseButton).setBackgroundImage(backupDatabaseBg);
    }
    // Hover Effects - Mouse Press
    private void backupDatabaseButtonMousePressed(MouseEvent e) {
        Image backupDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/backupDatabaseButtonPressed.png")).getImage();
        ((ImageButton) backupDatabaseButton).setBackgroundImage(backupDatabaseBg);
    }

    //
    // Restore Database Button Event Listener Methods
    //
    // Action Listener Method
    private void restoreDatabase(ActionEvent e) {
        try {
            String backupDir = DBConnection.getBackupDirectory();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Warning: This will overwrite the current database with the backup.\n" +
                            "Make sure you have a backup of your current data before proceeding.\n\n" +
                            "Do you want to continue?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser(backupDir);
                fileChooser.setDialogTitle("Select Backup File to Restore");
                fileChooser.setFileFilter(new FileNameExtensionFilter("SQL Files (*.sql)", "sql"));

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        DBConnection.restoreDatabase(fileChooser.getSelectedFile().getAbsolutePath());

                        JOptionPane.showMessageDialog(this,
                                "Database restored successfully!\nThe application will now close. Please restart it.",
                                "Restore Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        System.exit(0);
                    } catch (IOException | InterruptedException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error restoring backup: " + ex.getMessage(),
                                "Restore Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error accessing backup directory: " + ex.getMessage(),
                    "Directory Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    // Hover Effects - Mouse Enter
    private void restoreDatabaseButtonMouseEntered(MouseEvent e) {
        Image restoreDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/importDatabaseButtonActive.png")).getImage();
        ((ImageButton) restoreDatabaseButton).setBackgroundImage(restoreDatabaseBg);
    }
    // Hover Effects - Mouse Exit
    private void restoreDatabaseButtonMouseExited(MouseEvent e) {
        Image restoreDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/importDatabaseButton.png")).getImage();
        ((ImageButton) restoreDatabaseButton).setBackgroundImage(restoreDatabaseBg);
    }
    // Hover Effects - Mouse Press
    private void restoreDatabaseButtonMousePressed(MouseEvent e) {
        Image restoreDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/importDatabaseButtonPressed.png")).getImage();
        ((ImageButton) restoreDatabaseButton).setBackgroundImage(restoreDatabaseBg);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        appNameLabel = new JLabel();
        appNameSubLabel = new JLabel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        dateLabel = new JTextField();
        dashboardPanel1 = new JPanel();
        dashboardPanel1Container1 = new JPanel();
        totalSalesPanel = new JPanel();
        totalSalesLabel = new JTextField();
        totalSalesPlaceholder = new JTextField();
        dashboardPanel1Container2 = new JPanel();
        totalExpensesPanel = new JPanel();
        totalExpensesLabel = new JTextField();
        totalExpensesPlaceholder = new JTextField();
        dashboardPanel1Container3 = new JPanel();
        totalOrdersPanel = new JPanel();
        totalOrdersLabel = new JTextField();
        totalOrdersPlaceholder = new JTextField();
        dashboardPanel1Container4 = new JPanel();
        totalProductsPanel = new JPanel();
        totalProductsLabel = new JTextField();
        totalProductsPlaceholder = new JTextField();
        salesExpensesPanel = new JPanel();
        dashboardLabel2 = new JTextField();
        scrollPane1 = new JScrollPane();
        transactionHistoryTable = new JTable();
        stockAlertPanel = new JPanel();
        scrollPane2 = new JScrollPane();
        lowStockTable = new JTable();
        dashboardLabel3 = new JTextField();

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

            //---- userManagementButton ----
            userManagementButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            userManagementButton.setForeground(new Color(0x6c39c1));
            userManagementButton.setBackground(new Color(0x6c39c1));
            userManagementButton.setBorder(null);
            userManagementButton.setHorizontalAlignment(SwingConstants.LEFT);
            userManagementButton.setFocusable(false);
            userManagementButton.setBorderPainted(false);
            userManagementButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            userManagementButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    userManagementButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    userManagementButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    userManagementButtonMousePressed(e);
                }
            });
            userManagementButton.addActionListener(e -> userManagement(e));

            //---- discountCodesButton ----
            discountCodesButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            discountCodesButton.setForeground(new Color(0x6c39c1));
            discountCodesButton.setBackground(new Color(0x6c39c1));
            discountCodesButton.setBorder(null);
            discountCodesButton.setHorizontalAlignment(SwingConstants.LEFT);
            discountCodesButton.setFocusable(false);
            discountCodesButton.setBorderPainted(false);
            discountCodesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            discountCodesButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    discountCodesButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    discountCodesButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    discountCodesButtonMousePressed(e);
                }
            });
            discountCodesButton.addActionListener(e -> discountCodes(e));

            //---- backupDatabaseButton ----
            backupDatabaseButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            backupDatabaseButton.setForeground(new Color(0x6c39c1));
            backupDatabaseButton.setBackground(new Color(0x6c39c1));
            backupDatabaseButton.setBorder(null);
            backupDatabaseButton.setHorizontalAlignment(SwingConstants.LEFT);
            backupDatabaseButton.setFocusable(false);
            backupDatabaseButton.setBorderPainted(false);
            backupDatabaseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backupDatabaseButton.addActionListener(e -> backupDatabase(e));
            backupDatabaseButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    backupDatabaseButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    backupDatabaseButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    backupDatabaseButtonMousePressed(e);
                }
            });

            //---- restoreDatabaseButton ----
            restoreDatabaseButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            restoreDatabaseButton.setForeground(new Color(0x6c39c1));
            restoreDatabaseButton.setBackground(new Color(0x6c39c1));
            restoreDatabaseButton.setBorder(null);
            restoreDatabaseButton.setHorizontalAlignment(SwingConstants.LEFT);
            restoreDatabaseButton.setFocusable(false);
            restoreDatabaseButton.setBorderPainted(false);
            restoreDatabaseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            restoreDatabaseButton.addActionListener(e -> restoreDatabase(e));
            restoreDatabaseButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    restoreDatabaseButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    restoreDatabaseButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    restoreDatabaseButtonMousePressed(e);
                }
            });

            GroupLayout sidePanelLayout = new GroupLayout(sidePanel);
            sidePanel.setLayout(sidePanelLayout);
            sidePanelLayout.setHorizontalGroup(
                sidePanelLayout.createParallelGroup()
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addContainerGap(19, Short.MAX_VALUE)
                        .addGroup(sidePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(discountCodesButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addComponent(userManagementButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                            .addGroup(sidePanelLayout.createParallelGroup()
                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                .addGroup(sidePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(appNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(appNameSubLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inventoryButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                                    .addComponent(dashboardButton, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                                .addComponent(salesButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                .addComponent(resupplyButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                .addComponent(financialsButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE))
                            .addGroup(GroupLayout.Alignment.LEADING, sidePanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(backupDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(restoreDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)))
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
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userManagementButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(discountCodesButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                        .addGroup(sidePanelLayout.createParallelGroup()
                            .addComponent(backupDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                            .addComponent(restoreDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
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

            //---- dateLabel ----
            dateLabel.setText("Date Placeholder");
            dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dateLabel.setBackground(new Color(0xfcf8ff));
            dateLabel.setForeground(new Color(0x251779));
            dateLabel.setBorder(null);
            dateLabel.setFocusable(false);
            dateLabel.setEditable(false);
            dateLabel.setHorizontalAlignment(SwingConstants.TRAILING);

            GroupLayout windowTitleContainerLayout = new GroupLayout(windowTitleContainer);
            windowTitleContainer.setLayout(windowTitleContainerLayout);
            windowTitleContainerLayout.setHorizontalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 782, Short.MAX_VALUE)
                        .addComponent(dateLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(windowTitleContainerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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

                    //---- totalSalesLabel ----
                    totalSalesLabel.setText("Total Sales (Today)");
                    totalSalesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    totalSalesLabel.setForeground(new Color(0x251779));
                    totalSalesLabel.setBorder(null);
                    totalSalesLabel.setFocusable(false);
                    totalSalesLabel.setEditable(false);
                    totalSalesLabel.setBackground(new Color(0xe8e7f4));

                    //---- totalSalesPlaceholder ----
                    totalSalesPlaceholder.setText("0");
                    totalSalesPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    totalSalesPlaceholder.setForeground(new Color(0x251779));
                    totalSalesPlaceholder.setBorder(null);
                    totalSalesPlaceholder.setFocusable(false);
                    totalSalesPlaceholder.setEditable(false);
                    totalSalesPlaceholder.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalSalesPanelLayout = new GroupLayout(totalSalesPanel);
                    totalSalesPanel.setLayout(totalSalesPanelLayout);
                    totalSalesPanelLayout.setHorizontalGroup(
                        totalSalesPanelLayout.createParallelGroup()
                            .addGroup(totalSalesPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(totalSalesPanelLayout.createParallelGroup()
                                    .addComponent(totalSalesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalSalesPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(66, Short.MAX_VALUE))
                    );
                    totalSalesPanelLayout.setVerticalGroup(
                        totalSalesPanelLayout.createParallelGroup()
                            .addGroup(totalSalesPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(totalSalesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(totalSalesPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(65, Short.MAX_VALUE))
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

                    //---- totalExpensesLabel ----
                    totalExpensesLabel.setText("Total Expenses (Today)");
                    totalExpensesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    totalExpensesLabel.setForeground(new Color(0x251779));
                    totalExpensesLabel.setBorder(null);
                    totalExpensesLabel.setFocusable(false);
                    totalExpensesLabel.setEditable(false);
                    totalExpensesLabel.setBackground(new Color(0xe8e7f4));

                    //---- totalExpensesPlaceholder ----
                    totalExpensesPlaceholder.setText("0");
                    totalExpensesPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    totalExpensesPlaceholder.setForeground(new Color(0x251779));
                    totalExpensesPlaceholder.setBorder(null);
                    totalExpensesPlaceholder.setFocusable(false);
                    totalExpensesPlaceholder.setEditable(false);
                    totalExpensesPlaceholder.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalExpensesPanelLayout = new GroupLayout(totalExpensesPanel);
                    totalExpensesPanel.setLayout(totalExpensesPanelLayout);
                    totalExpensesPanelLayout.setHorizontalGroup(
                        totalExpensesPanelLayout.createParallelGroup()
                            .addGroup(totalExpensesPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(totalExpensesPanelLayout.createParallelGroup()
                                    .addComponent(totalExpensesPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalExpensesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(42, Short.MAX_VALUE))
                    );
                    totalExpensesPanelLayout.setVerticalGroup(
                        totalExpensesPanelLayout.createParallelGroup()
                            .addGroup(totalExpensesPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(totalExpensesLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(totalExpensesPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(65, Short.MAX_VALUE))
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

                    //---- totalOrdersLabel ----
                    totalOrdersLabel.setText("Total Orders");
                    totalOrdersLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    totalOrdersLabel.setForeground(new Color(0x251779));
                    totalOrdersLabel.setBorder(null);
                    totalOrdersLabel.setFocusable(false);
                    totalOrdersLabel.setEditable(false);
                    totalOrdersLabel.setBackground(new Color(0xe8e7f4));

                    //---- totalOrdersPlaceholder ----
                    totalOrdersPlaceholder.setText("0");
                    totalOrdersPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    totalOrdersPlaceholder.setForeground(new Color(0x251779));
                    totalOrdersPlaceholder.setBorder(null);
                    totalOrdersPlaceholder.setFocusable(false);
                    totalOrdersPlaceholder.setEditable(false);
                    totalOrdersPlaceholder.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalOrdersPanelLayout = new GroupLayout(totalOrdersPanel);
                    totalOrdersPanel.setLayout(totalOrdersPanelLayout);
                    totalOrdersPanelLayout.setHorizontalGroup(
                        totalOrdersPanelLayout.createParallelGroup()
                            .addGroup(totalOrdersPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(totalOrdersPanelLayout.createParallelGroup()
                                    .addComponent(totalOrdersPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalOrdersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(130, Short.MAX_VALUE))
                    );
                    totalOrdersPanelLayout.setVerticalGroup(
                        totalOrdersPanelLayout.createParallelGroup()
                            .addGroup(totalOrdersPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(totalOrdersLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(totalOrdersPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(65, Short.MAX_VALUE))
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

                    //---- totalProductsLabel ----
                    totalProductsLabel.setText("Total Products");
                    totalProductsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    totalProductsLabel.setBackground(new Color(0xe8e7f4));
                    totalProductsLabel.setForeground(new Color(0x251779));
                    totalProductsLabel.setBorder(null);
                    totalProductsLabel.setFocusable(false);
                    totalProductsLabel.setEditable(false);

                    //---- totalProductsPlaceholder ----
                    totalProductsPlaceholder.setText("0");
                    totalProductsPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    totalProductsPlaceholder.setForeground(new Color(0x251779));
                    totalProductsPlaceholder.setBorder(null);
                    totalProductsPlaceholder.setFocusable(false);
                    totalProductsPlaceholder.setEditable(false);
                    totalProductsPlaceholder.setBackground(new Color(0xe8e7f4));

                    GroupLayout totalProductsPanelLayout = new GroupLayout(totalProductsPanel);
                    totalProductsPanel.setLayout(totalProductsPanelLayout);
                    totalProductsPanelLayout.setHorizontalGroup(
                        totalProductsPanelLayout.createParallelGroup()
                            .addGroup(totalProductsPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(totalProductsPanelLayout.createParallelGroup()
                                    .addComponent(totalProductsPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalProductsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(102, Short.MAX_VALUE))
                    );
                    totalProductsPanelLayout.setVerticalGroup(
                        totalProductsPanelLayout.createParallelGroup()
                            .addGroup(totalProductsPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(totalProductsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(totalProductsPlaceholder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(65, Short.MAX_VALUE))
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
                                .addGap(0, 479, Short.MAX_VALUE)))
                        .addGap(20, 20, 20))
            );
            salesExpensesPanelLayout.setVerticalGroup(
                salesExpensesPanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, salesExpensesPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
            );
        }

        //======== stockAlertPanel ========
        {
            stockAlertPanel.setBackground(new Color(0xfcf8ff));

            //======== scrollPane2 ========
            {

                //---- lowStockTable ----
                lowStockTable.setRowHeight(40);
                lowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lowStockTable.setFocusable(false);
                scrollPane2.setViewportView(lowStockTable);
            }

            //---- dashboardLabel3 ----
            dashboardLabel3.setText("Low Stock Alerts");
            dashboardLabel3.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dashboardLabel3.setBackground(new Color(0xfcf8ff));
            dashboardLabel3.setForeground(new Color(0x251779));
            dashboardLabel3.setBorder(null);
            dashboardLabel3.setFocusable(false);
            dashboardLabel3.setEditable(false);

            GroupLayout stockAlertPanelLayout = new GroupLayout(stockAlertPanel);
            stockAlertPanel.setLayout(stockAlertPanelLayout);
            stockAlertPanelLayout.setHorizontalGroup(
                stockAlertPanelLayout.createParallelGroup()
                    .addGroup(stockAlertPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(stockAlertPanelLayout.createParallelGroup()
                            .addComponent(dashboardLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(20, Short.MAX_VALUE))
            );
            stockAlertPanelLayout.setVerticalGroup(
                stockAlertPanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, stockAlertPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
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
    private JButton resupplyButton;
    private JButton exitButton;
    private JButton financialsButton;
    private JButton userManagementButton;
    private JButton discountCodesButton;
    private JButton backupDatabaseButton;
    private JButton restoreDatabaseButton;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JTextField dateLabel;
    private JPanel dashboardPanel1;
    private JPanel dashboardPanel1Container1;
    private JPanel totalSalesPanel;
    private JTextField totalSalesLabel;
    private JTextField totalSalesPlaceholder;
    private JPanel dashboardPanel1Container2;
    private JPanel totalExpensesPanel;
    private JTextField totalExpensesLabel;
    private JTextField totalExpensesPlaceholder;
    private JPanel dashboardPanel1Container3;
    private JPanel totalOrdersPanel;
    private JTextField totalOrdersLabel;
    private JTextField totalOrdersPlaceholder;
    private JPanel dashboardPanel1Container4;
    private JPanel totalProductsPanel;
    private JTextField totalProductsLabel;
    private JTextField totalProductsPlaceholder;
    private JPanel salesExpensesPanel;
    private JTextField dashboardLabel2;
    private JScrollPane scrollPane1;
    private JTable transactionHistoryTable;
    private JPanel stockAlertPanel;
    private JScrollPane scrollPane2;
    private JTable lowStockTable;
    private JTextField dashboardLabel3;
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

        // Lock Column Re-order
        transactionHistoryTable.getTableHeader().setReorderingAllowed(false);
    }

    //
    // SQL Functionalities Section
    //
    void populateTableRecentSales() {
        String sql = "SELECT transaction_id, date, customer_name, total_price " +
                "FROM transaction_history " +
                "ORDER BY date DESC LIMIT 20";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Update column names to match requested fields
            model.setColumnIdentifiers(new String[]{"Transaction ID", "Date", "Customer Name", "Total Price"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("transaction_id"), // Changed from getInt to getString
                        rs.getTimestamp("date"),
                        rs.getString("customer_name"),
                        String.format("%.2f", rs.getDouble("total_price"))
                });
            }

            transactionHistoryTable.setModel(model);
            setTableTheme();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading transaction history: " + ex.getMessage());
        }
    }

    private void updateDashboardStatistics() {
        updateTotalSales();
        updateTotalExpenses();
        updateTotalOrders();
        updateTotalProducts();
    }

    private void updateTotalSales() {
        String sql = "SELECT SUM(total_price) AS total_sales FROM transaction_history " +
                "WHERE DATE(date) = CURRENT_DATE()";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double totalSales = rs.getDouble("total_sales");
                if (rs.wasNull()) {
                    totalSalesPlaceholder.setText("0.00");
                } else {
                    totalSalesPlaceholder.setText(String.format("%.2f", totalSales));
                }
            } else {
                totalSalesPlaceholder.setText("0.00");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching today's sales: " + ex.getMessage());
            totalSalesPlaceholder.setText("0.00");
        }
    }

    private void updateTotalExpenses() {
        String sql = "SELECT SUM(total_cost) AS total_expenses FROM resupply_history " +
                "WHERE DATE(resupply_date) = CURRENT_DATE()";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double totalExpenses = rs.getDouble("total_expenses");
                if (rs.wasNull()) {
                    totalExpensesPlaceholder.setText("0.00");
                } else {
                    totalExpensesPlaceholder.setText(String.format("%.2f", totalExpenses));
                }
            } else {
                totalExpensesPlaceholder.setText("0.00");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching today's expenses: " + ex.getMessage());
            totalExpensesPlaceholder.setText("0.00");
        }
    }


    private void updateTotalOrders() {
        String sql = "SELECT COUNT(*) AS total_orders FROM transaction_history";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int totalOrders = rs.getInt("total_orders");
                totalOrdersPlaceholder.setText(String.valueOf(totalOrders));
            } else {
                totalOrdersPlaceholder.setText("0");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching total orders: " + ex.getMessage());
            totalOrdersPlaceholder.setText("0");
        }
    }

    private void updateTotalProducts() {
        String sql = "SELECT COUNT(*) AS total_products FROM inventory";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int totalProducts = rs.getInt("total_products");
                totalProductsPlaceholder.setText(String.valueOf(totalProducts));
            } else {
                totalProductsPlaceholder.setText("0");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching total products: " + ex.getMessage());
            totalProductsPlaceholder.setText("0");
        }
    }

    private void setCurrentDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        dateLabel.setText(today.format(formatter));
    }

    private void setupLowStockTable() {
        // Create table model
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Item Name", "Quantity"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        lowStockTable.setModel(model);
        lowStockTable.setShowGrid(false);

        // Custom cell renderer for low stock items
        DefaultTableCellRenderer lowStockRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Set font to bold
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));

                // Set background color
                if (isSelected) {
                    label.setBackground(Color.decode("#A59BDA"));
                } else {
                    label.setBackground(row % 2 == 0 ? Color.decode("#D4CFED") : Color.WHITE);
                }

                // Only try to parse as integer if it's the quantity column and not "N/A"
                if (column == 1 && value != null && !value.toString().equals("N/A")) {
                    try {
                        int quantity = Integer.parseInt(value.toString());

                        // Set text color based on quantity (redder as it gets closer to 0)
                        if (quantity <= 5) {
                            label.setForeground(new Color(200, 0, 0)); // Very red for critical level
                        } else if (quantity <= 10) {
                            label.setForeground(new Color(220, 30, 30)); // Medium red
                        } else {
                            label.setForeground(new Color(240, 60, 60)); // Light red
                        }
                    } catch (NumberFormatException e) {
                        // If it's not a valid number, use default color
                        label.setForeground(Color.BLACK);
                    }
                } else if (column == 0 && table.getValueAt(row, 1).toString().equals("N/A")) {
                    // For the "No low stock items" message row
                    label.setForeground(Color.BLACK);
                }

                // Apply left padding
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                label.setHorizontalAlignment(SwingConstants.LEFT);

                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < lowStockTable.getColumnCount(); i++) {
            lowStockTable.getColumnModel().getColumn(i).setCellRenderer(lowStockRenderer);
        }

        // Set Table Header Style
        JTableHeader header = lowStockTable.getTableHeader();
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
        lowStockTable.getTableHeader().setReorderingAllowed(false);

        // Populate the table with low stock items
        populateLowStockItems();
    }

    private void populateLowStockItems() {
        String sql = "SELECT item_name, quantity FROM inventory WHERE quantity < 20 ORDER BY quantity ASC";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) lowStockTable.getModel();

            // Clear existing data
            model.setRowCount(0);

            // Add new data
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("item_name"),
                        rs.getInt("quantity")
                });
            }

            // If no low stock items found, add a message row
            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"No low stock items", "N/A"});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading low stock items: " + ex.getMessage());
        }
    }
}
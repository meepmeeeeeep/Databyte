import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;

public class Financials extends JPanel {
    private ChartPanel revenueChartPanel;
    private ChartPanel categoryChartPanel;
    private ChartPanel costChartPanel;
    private ChartPanel profitChartPanel;
    private final SalesAnalysisChart analysisChart;
    public Financials() {
        // Use Custom Background Images for Side Panel Buttons
        //---- dashboardButton ----
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButton.png")).getImage();
        dashboardButton = new ImageButton(dashboardBg, "");

        //---- inventoryButton ----
        Image inventoryBg = new ImageIcon(getClass().getResource("/assets/images/inventoryButton.png")).getImage();
        inventoryButton = new ImageButton(inventoryBg, "");

        //---- salesButton ----
        Image salesBg = new ImageIcon(getClass().getResource("/assets/images/salesButton.png")).getImage();
        salesButton = new ImageButton(salesBg, "");

        //---- financialsButton ----
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButtonActive.png")).getImage();
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

        analysisChart = new SalesAnalysisChart();
        initComponents();
        setupChartComponents();

        // Add Left-Padding to Text Fields
        //---- Period Selection ----
        periodSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return comp;
            }
        });

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
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButton.png")).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Hover Effects - Mouse Press
    private void dashboardButtonMousePressed(MouseEvent e) {
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/dashboardButtonPressed.png")).getImage();
        ((ImageButton) dashboardButton).setBackgroundImage(dashboardBg);
    }
    // Action Listener Method
    private void dashboard(ActionEvent e) {
        // Open Inventory
        SwingUtilities.getWindowAncestor(this).dispose();; // Close Financials

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
        SwingUtilities.getWindowAncestor(this).dispose();; // Close Financials

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Financials

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
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButtonActive.png")).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
    }
    // Hover Effects - Mouse Press
    private void financialsButtonMousePressed(MouseEvent e) {
        Image financialsBg = new ImageIcon(getClass().getResource("/assets/images/financialsButtonActive.png")).getImage();
        ((ImageButton) financialsButton).setBackgroundImage(financialsBg);
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Financials

        JFrame frame = new JFrame("Resupply");
        frame.setContentPane(new Resupply());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void periodSelector(ActionEvent e) {
        updateCharts();
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Financials

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Financials

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
        controlsPanel = new JPanel();
        financialReportLabel = new JTextField();
        periodSelector = new JComboBox<>();
        chartsContainer = new JPanel();

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
                                .addComponent(financialsButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(backupDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(restoreDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(121, Short.MAX_VALUE))
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
            dashboardLabel.setText("Financials");
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
                        .addContainerGap(1039, Short.MAX_VALUE))
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

            //---- financialReportLabel ----
            financialReportLabel.setText("Select Period:");
            financialReportLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            financialReportLabel.setForeground(new Color(0x897cce));
            financialReportLabel.setBorder(null);
            financialReportLabel.setFocusable(false);
            financialReportLabel.setEditable(false);
            financialReportLabel.setBackground(new Color(0xfcf8ff));

            //---- periodSelector ----
            periodSelector.setModel(new DefaultComboBoxModel<>(new String[] {
                "WEEKLY",
                "MONTHLY",
                "YEARLY"
            }));
            periodSelector.setFocusable(false);
            periodSelector.setBorder(null);
            periodSelector.setBackground(new Color(0xe8e7f4));
            periodSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            periodSelector.addActionListener(e -> periodSelector(e));

            GroupLayout controlsPanelLayout = new GroupLayout(controlsPanel);
            controlsPanel.setLayout(controlsPanelLayout);
            controlsPanelLayout.setHorizontalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(financialReportLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(periodSelector, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(792, Short.MAX_VALUE))
            );
            controlsPanelLayout.setVerticalGroup(
                controlsPanelLayout.createParallelGroup()
                    .addGroup(controlsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(periodSelector, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(financialReportLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(9, Short.MAX_VALUE))
            );
        }

        //======== chartsContainer ========
        {
            chartsContainer.setBackground(new Color(0xe8e7f4));
            chartsContainer.setLayout(new GridLayout(2, 2, 20, 20));
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
                            .addGap(25, 25, 25)
                            .addComponent(chartsContainer, GroupLayout.DEFAULT_SIZE, 1090, Short.MAX_VALUE)
                            .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(controlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)
                    .addComponent(chartsContainer, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                    .addGap(25, 25, 25))
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
    private JPanel controlsPanel;
    private JTextField financialReportLabel;
    private JComboBox<String> periodSelector;
    private JPanel chartsContainer;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void setupChartComponents() {
        // Initialize chart panels
        revenueChartPanel = new ChartPanel(analysisChart.createRevenueLineChart("Weekly"));
        costChartPanel = new ChartPanel(analysisChart.createCostLineChart("Weekly"));
        profitChartPanel = new ChartPanel(analysisChart.createProfitLineChart("Weekly"));
        categoryChartPanel = new ChartPanel(analysisChart.createCategoryComparisonChart());

        // Set chart panel sizes
        Dimension chartSize = new Dimension(500, 300);
        revenueChartPanel.setPreferredSize(chartSize);
        costChartPanel.setPreferredSize(chartSize);
        profitChartPanel.setPreferredSize(chartSize);
        categoryChartPanel.setPreferredSize(chartSize);

        // Add charts in desired order
        chartsContainer.add(revenueChartPanel);  // Top left
        chartsContainer.add(costChartPanel);     // Top right
        chartsContainer.add(profitChartPanel);   // Bottom left
        chartsContainer.add(categoryChartPanel); // Bottom right
    }

    private void updateCharts() {
        String selectedPeriod = (String) periodSelector.getSelectedItem();
        // Convert to title case for proper comparison
        String formattedPeriod = selectedPeriod.substring(0, 1).toUpperCase() +
                selectedPeriod.substring(1).toLowerCase();

        revenueChartPanel.setChart(analysisChart.createRevenueLineChart(formattedPeriod));
        costChartPanel.setChart(analysisChart.createCostLineChart(formattedPeriod));
        profitChartPanel.setChart(analysisChart.createProfitLineChart(formattedPeriod));
        categoryChartPanel.setChart(analysisChart.createCategoryComparisonChart());

        // Force a repaint of the container
        chartsContainer.revalidate();
        chartsContainer.repaint();
    }
}

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Timer;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class UserManagement extends JPanel {
    private String currentLoggedInUser;
    public UserManagement() {
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
        Image resupplyBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/resupplyButton.png"))).getImage();
        resupplyButton = new ImageButton(resupplyBg, "");

        //---- userManagementButton ----
        Image userManagementBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/UserManagementButtonActive.png"))).getImage();
        userManagementButton = new ImageButton(userManagementBg, "");

        //---- userManagementButton ----
        Image discountCodesBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/discountCodesButton.png"))).getImage();
        discountCodesButton = new ImageButton(discountCodesBg, "");

        //---- exitButton ----
        Image exitBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/exitButton.png"))).getImage();
        exitButton = new ImageButton(exitBg, "");

        // Use Custom Background Images for User Management Buttons
        //---- addItemButton ----
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButton.png"))).getImage();
        createButton = new ImageButton(addItemBg, "");

        //---- deleteItemButton ----
        Image deleteItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/deleteItemButton.png"))).getImage();
        deleteButton = new ImageButton(deleteItemBg, "");

        //---- editItemButton ----
        Image editItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/editItemButton.png"))).getImage();
        editButton = new ImageButton(editItemBg, "");

        //---- refreshButton ----
        Image refreshBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/refreshButton.png"))).getImage();
        refreshButton = new ImageButton(refreshBg, "");

        //---- backupDatabaseButton ----
        Image backupDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/backupDatabaseButton.png")).getImage();
        backupDatabaseButton = new ImageButton(backupDatabaseBg, "");

        //---- restoreDatabaseButton ----
        Image restoreDatabaseBg = new ImageIcon(getClass().getResource("/assets/images/importDatabaseButton.png")).getImage();
        restoreDatabaseButton = new ImageButton(restoreDatabaseBg, "");

        initComponents();
        populateTable();

        // Add Left-Padding to Search Field
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));

        // Add a document listener to the search field for real-time filtering
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                populateTable(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                populateTable(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                populateTable(searchField.getText());
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

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
    private void inventory(ActionEvent e) {
        // Open Inventory
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

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
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButtonActive.png")).getImage();
        ((ImageButton) userManagementButton).setBackgroundImage(userManagementBg);
    }
    // Hover Effects - Mouse Press
    private void userManagementButtonMousePressed(MouseEvent e) {
        Image userManagementBg = new ImageIcon(getClass().getResource("/assets/images/UserManagementButtonActive.png")).getImage();
        ((ImageButton) userManagementButton).setBackgroundImage(userManagementBg);
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
        SwingUtilities.getWindowAncestor(this).dispose(); // Close User Management

        JFrame frame = new JFrame("Discount Codes");
        frame.setContentPane(new DiscountCodes());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    //
    // Add Item Button Event Listener Methods
    //
    // Action Listener Method
    private void create(ActionEvent e) {
        JFrame frame = new JFrame("Create User");
        frame.setContentPane(new CreateUserForm(this));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);          // Disable window resizing
        frame.setVisible(true);
    }
    // Hover Effects - Mouse Enter
    private void createButtonMouseEntered(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButtonActive.png"))).getImage();
        ((ImageButton) createButton).setBackgroundImage(addItemBg);
    }
    // Hover Effects - Mouse Exit
    private void createButtonMouseExited(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButton.png"))).getImage();
        ((ImageButton) createButton).setBackgroundImage(addItemBg);
    }
    // Hover Effects - Mouse Press
    private void createButtonMousePressed(MouseEvent e) {
        Image addItemBg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/images/addItemButtonPressed.png"))).getImage();
        ((ImageButton) createButton).setBackgroundImage(addItemBg);
    }

    //
    // Delete Item Button Event Listener Methods
    //
    // Action Listener Method
    private void delete(ActionEvent e) {
        deleteSelectedUser();
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
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            String username = usersTable.getValueAt(selectedRow, 1).toString();

            // Additional checks for current user and admin
            if (username.equals(currentLoggedInUser)) {
                JOptionPane.showMessageDialog(this, "You cannot edit your own account.", "Operation Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.equals("admin")) {
                JOptionPane.showMessageDialog(this, "The admin account cannot be edited.", "Operation Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFrame frame = new JFrame("Editing User " + username);
            frame.setContentPane(new EditUserForm(username, this));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a user to edit.");
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
        searchField = new JTextField();
        scrollPane1 = new JScrollPane();
        usersTable = new JTable();

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
                        .addGap(44, 44, 44)
                        .addComponent(backupDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(restoreDatabaseButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(120, Short.MAX_VALUE))
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
            dashboardLabel.setText("User Management");
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
                        .addContainerGap(966, Short.MAX_VALUE))
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

            //---- createButton ----
            createButton.setFocusable(false);
            createButton.addActionListener(e -> create(e));
            createButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    createButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    createButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    createButtonMousePressed(e);
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
                        .addComponent(createButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(createButton, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
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

            //---- usersTable ----
            usersTable.setRowHeight(40);
            usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            scrollPane1.setViewportView(usersTable);
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
    private JTextField searchField;
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton createButton;
    private JScrollPane scrollPane1;
    private JTable usersTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


    //
    // SQL Functionalities Section
    //
    // Refresh/Populate Table
    void populateTable() {
        populateTable("");
    }

    void populateTable(String searchQuery) {
        String sql = "SELECT id, username, role, email, contact_number " +
                "FROM users " +
                "WHERE username LIKE ? OR role LIKE ? OR email LIKE ? OR contact_number LIKE ? " +
                "ORDER BY id";

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String wildcardQuery = "%" + searchQuery + "%";
            pst.setString(1, wildcardQuery);
            pst.setString(2, wildcardQuery);
            pst.setString(3, wildcardQuery);
            pst.setString(4, wildcardQuery);

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.setColumnIdentifiers(new String[]{"ID", "Username", "Role", "Email", "Contact Number"});

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("contact_number")
                });
            }

            usersTable.setModel(model);

            // Add custom selection model to prevent selecting current user's row
            usersTable.setSelectionModel(new DefaultListSelectionModel() {
                @Override
                public void setSelectionInterval(int index0, int index1) {
                    String username = (String) usersTable.getValueAt(index0, 1);
                    if (!username.equals(currentLoggedInUser) && !username.equals("admin")) {
                        super.setSelectionInterval(index0, index1);
                    }
                }
            });

            setTableTheme();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + ex.getMessage());
        }
    }

    // Delete User
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) usersTable.getValueAt(selectedRow, 1);

        // Additional checks for current user and admin
        if (username.equals(currentLoggedInUser)) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.", "Operation Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "The admin account cannot be deleted.", "Operation Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the user id from the selected row
        int userId = (int) usersTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM users WHERE id = ?";

            try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                    populateTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "User could not be deleted. Please try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //
// Set Table Theme/Layout
//
    private void setTableTheme() {
        // Use Custom Theme for Users Table
        usersTable.setShowGrid(false);
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
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Set Table Header Style
        JTableHeader header = usersTable.getTableHeader();
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
        TableColumn[] columns = {
                usersTable.getColumnModel().getColumn(0), // ID
                usersTable.getColumnModel().getColumn(1), // Username
                usersTable.getColumnModel().getColumn(2), // Role
                usersTable.getColumnModel().getColumn(3), // Email
                usersTable.getColumnModel().getColumn(4)  // Contact Number
        };

        int[] preferredWidths = {50, 200, 150, 250, 150};
        int[] minWidths = {50, 150, 100, 200, 150};

        for (int i = 0; i < columns.length; i++) {
            columns[i].setPreferredWidth(preferredWidths[i]);
            columns[i].setMinWidth(minWidths[i]);
            if (i == 0) {
                columns[i].setMaxWidth(50);
                columns[i].setResizable(false);
            }
        }

        // Lock Column Re-order
        usersTable.getTableHeader().setReorderingAllowed(false);
    }
}

// OderDetails.java

import java.awt.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.table.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OrderDetailsForm extends JPanel {

    private final String employeeName;

    public OrderDetailsForm(String transactionId,
                            Timestamp date,
                            String employeeName,
                            String customerName,
                            String customerAddress,
                            String customerEmail,
                            String customerPhone,
                            double totalPrice,
                            double paymentAmount,
                            String paymentMethod,
                            String discountCode,
                            Object[][] cartData) {

        // Use Custom Background Images for Buttons
        //---- printButton ----
        Image printBg = new ImageIcon(getClass().getResource("/assets/images/printButton.png")).getImage();
        printButton = new ImageButton(printBg, "");

        initComponents();

        // Add Left-Padding to Text Fields
        //---- Customer Information ----
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
                customerNameField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerAddressField.setBorder(BorderFactory.createCompoundBorder(
                customerAddressField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerEmailField.setBorder(BorderFactory.createCompoundBorder(
                customerEmailField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        customerPhoneField.setBorder(BorderFactory.createCompoundBorder(
                customerPhoneField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        //---- Payment Details ----
        totalAmountField.setBorder(BorderFactory.createCompoundBorder(
                totalAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentAmountField.setBorder(BorderFactory.createCompoundBorder(
                paymentAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentMethodField.setBorder(BorderFactory.createCompoundBorder(
                paymentMethodField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        discountCodeField.setBorder(BorderFactory.createCompoundBorder(
                discountCodeField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));

        // Set Form Label text
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dashboardLabel.setText("Order Details - Transaction ID: " + transactionId + " | Date: " + sdf.format(date));

        // Set employee name
        this.employeeName = employeeName;
        
        // Set customer information
        customerNameField.setText(customerName);
        customerAddressField.setText(customerAddress);
        customerEmailField.setText(customerEmail);
        customerPhoneField.setText(customerPhone);

        // Set payment details
        totalAmountField.setText(String.format("%.2f", totalPrice));
        paymentAmountField.setText(String.format("%.2f", paymentAmount));
        paymentMethodField.setText(paymentMethod);
        discountCodeField.setText(discountCode);

        // Set up cart table
        setupCartTable(cartData);

        // Make table rows non-selectable
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowSelectionAllowed(false);
        cartTable.setCellSelectionEnabled(false);
        cartTable.getTableHeader().setReorderingAllowed(false);
        cartTable.setFocusable(false);
    }

    //
    // Print Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void printButtonMouseEntered(MouseEvent e) {
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/printButtonActive.png")).getImage();
        ((ImageButton) printButton).setBackgroundImage(exitBg);
    }
    // Hover Effects - Mouse Exit
    private void printButtonMouseExited(MouseEvent e) {
        Image exitBg = new ImageIcon(getClass().getResource("/assets/images/printButton.png")).getImage();
        ((ImageButton) printButton).setBackgroundImage(exitBg);
    }
    private void printButtonMousePressed(MouseEvent e) {
        Image dashboardBg = new ImageIcon(getClass().getResource("/assets/images/printButtonPressed.png")).getImage();
        ((ImageButton) printButton).setBackgroundImage(dashboardBg);
    }

    private void printReceipt(ActionEvent e) {
        try {
            Document document = new Document(com.itextpdf.text.PageSize.A4);
            document.setPageSize(new com.itextpdf.text.Rectangle(226.772f, 842f)); // 80mm width

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Receipt");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Define consistent fonts
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 8, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 7, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 6, com.itextpdf.text.Font.NORMAL);

                // Store header
                Paragraph header = new Paragraph();
                header.setAlignment(Element.ALIGN_CENTER);
                header.add(new Chunk("Databyte\n", titleFont));
                header.add(new Chunk("123 Main Street\n", smallFont));
                header.add(new Chunk("Tel: (123) 456-7890\n\n", smallFont));
                document.add(header);

                // Transaction details
                document.add(new Paragraph("Transaction ID: " + dashboardLabel.getText().split(":")[1].split("\\|")[0].trim(), normalFont));
                document.add(new Paragraph("Date: " + dashboardLabel.getText().split("Date:")[1].trim(), normalFont));
                document.add(new Paragraph("Cashier Name: " + employeeName, normalFont));
                document.add(new Paragraph("\nCustomer Information:", titleFont));
                document.add(new Paragraph("Name: " + customerNameField.getText(), normalFont));

                if (!customerPhoneField.getText().isEmpty()) {
                    document.add(new Paragraph("Contact: " + customerPhoneField.getText(), normalFont));
                }
                if (!customerAddressField.getText().isEmpty()) {
                    document.add(new Paragraph("Address: " + customerAddressField.getText(), normalFont));
                }

                // Payment Method and Discount Code in Order Details
                document.add(new Paragraph("\nOrder Details", titleFont));
                document.add(new Paragraph("Payment Method: " + paymentMethodField.getText(), normalFont));

                document.add(new Paragraph("", normalFont)); // Empty line for spacing

                // Items from cart table
                DefaultTableModel model = (DefaultTableModel) cartTable.getModel();

                double originalTotal = 0.0;  // Track the original total before discount

                for (int i = 0; i < model.getRowCount(); i++) {
                    String itemId = model.getValueAt(i, 0).toString();
                    String itemName = model.getValueAt(i, 1).toString();
                    int quantity = Integer.parseInt(model.getValueAt(i, 6).toString());
                    double unitPrice = Double.parseDouble(model.getValueAt(i, 3).toString());
                    double vatPrice = Double.parseDouble(model.getValueAt(i, 5).toString());

                    // Calculate subtotal using VAT inclusive price
                    double subtotal = unitPrice * quantity;
                    originalTotal += subtotal;

                    // Item details with aligned prices
                    document.add(new Paragraph(itemId + " - " +
                            (itemName.length() > 20 ? itemName.substring(0, 17) + "..." : itemName), normalFont));

                    Paragraph priceDetails = new Paragraph();
                    priceDetails.setIndentationLeft(10);
                    priceDetails.add(new Chunk(String.format("x%d @ %8.2f\n", quantity, unitPrice), smallFont));
                    priceDetails.add(new Chunk(String.format("VAT excl.: %8.2f\n", vatPrice), smallFont));
                    priceDetails.add(new Chunk(String.format("Sub: %8.2f\n", subtotal), smallFont));
                    document.add(priceDetails);
                }

                // Totals section
                document.add(new Paragraph("\n", normalFont));
                double total = Double.parseDouble(totalAmountField.getText());
                double payment = Double.parseDouble(paymentAmountField.getText());
                double change = payment - total;

                double finalTotal = Double.parseDouble(totalAmountField.getText());

                if (!discountCodeField.getText().isEmpty()) {
                    double discountAmount = originalTotal - finalTotal;

                    Paragraph discountInfo = new Paragraph();
                    discountInfo.setAlignment(Element.ALIGN_RIGHT);
                    discountInfo.add(new Chunk(String.format("Original Total: %8.2f\n", originalTotal), normalFont));
                    discountInfo.add(new Chunk(String.format("Discount (%s): %8.2f\n", discountCodeField.getText(), discountAmount), normalFont));
                    document.add(discountInfo);
                }

                Paragraph totals = new Paragraph();
                totals.setAlignment(Element.ALIGN_RIGHT);
                totals.add(new Chunk(String.format("Total: %8.2f\n", finalTotal), titleFont));
                totals.add(new Chunk(String.format("Paid: %8.2f\n", payment), normalFont));

                // Display Change if Payment Method is CASH
                if (paymentMethodField.getText().equalsIgnoreCase("CASH")) {
                    totals.add(new Chunk(String.format("Change: %8.2f\n", change), normalFont));
                }

                document.add(totals);

                document.add(new Paragraph("\nThank you for shopping!", smallFont));
                document.close();

                JOptionPane.showMessageDialog(this,
                        "Receipt saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating receipt: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        customerNameField = new JTextField();
        customeNameLabel = new JTextField();
        customerAddressLabel = new JTextField();
        customerAddressField = new JTextField();
        customerEmailLabel = new JTextField();
        customerEmailField = new JTextField();
        customerPhoneField = new JTextField();
        customerPhoneLabel = new JTextField();
        customerInformationLabel = new JTextField();
        paymentDetailsLabel = new JTextField();
        totalAmountLabel = new JTextField();
        totalAmountField = new JTextField();
        paymentAmountLabel = new JTextField();
        paymentAmountField = new JTextField();
        paymentMethodLabel = new JTextField();
        paymentMethodField = new JTextField();
        panel2 = new JPanel();
        cartLabel = new JTextField();
        scrollPane1 = new JScrollPane();
        cartTable = new JTable();
        discountCodeLabel = new JTextField();
        discountCodeField = new JTextField();

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
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //======== windowTitleContainer ========
        {
            windowTitleContainer.setBackground(new Color(0xfcf8ff));

            //---- dashboardLabel ----
            dashboardLabel.setText("Order Details");
            dashboardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dashboardLabel.setBackground(new Color(0xfcf8ff));
            dashboardLabel.setForeground(new Color(0x251779));
            dashboardLabel.setBorder(null);
            dashboardLabel.setFocusable(false);
            dashboardLabel.setEditable(false);

            //---- printButton ----
            printButton.setBackground(new Color(0x6c39c1));
            printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            printButton.setForeground(new Color(0xfcf8ff));
            printButton.setFocusable(false);
            printButton.addActionListener(e -> printReceipt(e));
            printButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    printButtonMouseEntered(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    printButtonMouseExited(e);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    printButtonMousePressed(e);
                }
            });

            GroupLayout windowTitleContainerLayout = new GroupLayout(windowTitleContainer);
            windowTitleContainer.setLayout(windowTitleContainerLayout);
            windowTitleContainerLayout.setHorizontalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 1181, Short.MAX_VALUE)
                        .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
            );
            windowTitleContainerLayout.setVerticalGroup(
                windowTitleContainerLayout.createParallelGroup()
                    .addGroup(windowTitleContainerLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(windowTitleContainerLayout.createParallelGroup()
                            .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(9, Short.MAX_VALUE))
            );
        }

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0xfcf8ff));

            //---- customerNameField ----
            customerNameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerNameField.setBackground(new Color(0xe8e7f4));
            customerNameField.setEditable(false);
            customerNameField.setFocusable(false);
            customerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customeNameLabel ----
            customeNameLabel.setText("Customer Name:");
            customeNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customeNameLabel.setBackground(new Color(0xfcf8ff));
            customeNameLabel.setForeground(new Color(0x897cce));
            customeNameLabel.setBorder(null);
            customeNameLabel.setFocusable(false);
            customeNameLabel.setEditable(false);

            //---- customerAddressLabel ----
            customerAddressLabel.setText("Customer Address: (Optional)");
            customerAddressLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerAddressLabel.setBackground(new Color(0xfcf8ff));
            customerAddressLabel.setForeground(new Color(0x897cce));
            customerAddressLabel.setBorder(null);
            customerAddressLabel.setFocusable(false);
            customerAddressLabel.setEditable(false);

            //---- customerAddressField ----
            customerAddressField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerAddressField.setBackground(new Color(0xe8e7f4));
            customerAddressField.setEditable(false);
            customerAddressField.setFocusable(false);
            customerAddressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerEmailLabel ----
            customerEmailLabel.setText("Customer Email: (Optional)");
            customerEmailLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerEmailLabel.setBackground(new Color(0xfcf8ff));
            customerEmailLabel.setForeground(new Color(0x897cce));
            customerEmailLabel.setBorder(null);
            customerEmailLabel.setFocusable(false);
            customerEmailLabel.setEditable(false);

            //---- customerEmailField ----
            customerEmailField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerEmailField.setBackground(new Color(0xe8e7f4));
            customerEmailField.setEditable(false);
            customerEmailField.setFocusable(false);
            customerEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerPhoneField ----
            customerPhoneField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            customerPhoneField.setBackground(new Color(0xe8e7f4));
            customerPhoneField.setEditable(false);
            customerPhoneField.setFocusable(false);
            customerPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- customerPhoneLabel ----
            customerPhoneLabel.setText("Customer Phone Number: (Optional)");
            customerPhoneLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            customerPhoneLabel.setBackground(new Color(0xfcf8ff));
            customerPhoneLabel.setForeground(new Color(0x897cce));
            customerPhoneLabel.setBorder(null);
            customerPhoneLabel.setFocusable(false);
            customerPhoneLabel.setEditable(false);

            //---- customerInformationLabel ----
            customerInformationLabel.setText("Customer Information");
            customerInformationLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            customerInformationLabel.setBackground(new Color(0xfcf8ff));
            customerInformationLabel.setForeground(new Color(0x251779));
            customerInformationLabel.setBorder(null);
            customerInformationLabel.setFocusable(false);
            customerInformationLabel.setEditable(false);

            //---- paymentDetailsLabel ----
            paymentDetailsLabel.setText("Payment Details");
            paymentDetailsLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            paymentDetailsLabel.setBackground(new Color(0xfcf8ff));
            paymentDetailsLabel.setForeground(new Color(0x251779));
            paymentDetailsLabel.setBorder(null);
            paymentDetailsLabel.setFocusable(false);
            paymentDetailsLabel.setEditable(false);

            //---- totalAmountLabel ----
            totalAmountLabel.setText("Total Amount:");
            totalAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            totalAmountLabel.setBackground(new Color(0xfcf8ff));
            totalAmountLabel.setForeground(new Color(0x897cce));
            totalAmountLabel.setBorder(null);
            totalAmountLabel.setFocusable(false);
            totalAmountLabel.setEditable(false);

            //---- totalAmountField ----
            totalAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            totalAmountField.setEditable(false);
            totalAmountField.setFocusable(false);
            totalAmountField.setBackground(new Color(0xe8e7f4));
            totalAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- paymentAmountLabel ----
            paymentAmountLabel.setText("Payment Amount:");
            paymentAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            paymentAmountLabel.setBackground(new Color(0xfcf8ff));
            paymentAmountLabel.setForeground(new Color(0x897cce));
            paymentAmountLabel.setBorder(null);
            paymentAmountLabel.setFocusable(false);
            paymentAmountLabel.setEditable(false);

            //---- paymentAmountField ----
            paymentAmountField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            paymentAmountField.setBackground(new Color(0xe8e7f4));
            paymentAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            paymentAmountField.setEditable(false);
            paymentAmountField.setFocusable(false);

            //---- paymentMethodLabel ----
            paymentMethodLabel.setText("Payment Method:");
            paymentMethodLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            paymentMethodLabel.setBackground(new Color(0xfcf8ff));
            paymentMethodLabel.setForeground(new Color(0x897cce));
            paymentMethodLabel.setBorder(null);
            paymentMethodLabel.setFocusable(false);
            paymentMethodLabel.setEditable(false);

            //---- paymentMethodField ----
            paymentMethodField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            paymentMethodField.setEditable(false);
            paymentMethodField.setFocusable(false);
            paymentMethodField.setBackground(new Color(0xe8e7f4));
            paymentMethodField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                                        .addGap(50, 50, 50)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 20, Short.MAX_VALUE))))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(customerInformationLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customeNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customerNameField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(customerAddressLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customerAddressField, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(customerEmailLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customerEmailField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(customerPhoneLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(customerPhoneField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 43, 43)
                        .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(23, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBackground(new Color(0xfcf8ff));

            //---- cartLabel ----
            cartLabel.setText("Cart");
            cartLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            cartLabel.setBackground(new Color(0xfcf8ff));
            cartLabel.setForeground(new Color(0x251779));
            cartLabel.setBorder(null);
            cartLabel.setFocusable(false);
            cartLabel.setEditable(false);

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(new Color(0xfcf8ff));
                scrollPane1.setViewportView(cartTable);
            }

            //---- discountCodeLabel ----
            discountCodeLabel.setText("Discount Code:");
            discountCodeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            discountCodeLabel.setBackground(new Color(0xfcf8ff));
            discountCodeLabel.setForeground(new Color(0x897cce));
            discountCodeLabel.setBorder(null);
            discountCodeLabel.setFocusable(false);
            discountCodeLabel.setEditable(false);

            //---- discountCodeField ----
            discountCodeField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            discountCodeField.setBackground(new Color(0xe8e7f4));
            discountCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            discountCodeField.setEditable(false);
            discountCodeField.setFocusable(false);

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 394, Short.MAX_VALUE))
                            .addComponent(discountCodeField, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                        .addGap(20, 20, 20))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(cartLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                        .addGap(20, 20, 20)
                        .addComponent(discountCodeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(discountCodeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
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
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(windowTitleContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(25, 25, 25))
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JButton printButton;
    private JPanel panel1;
    private JTextField customerNameField;
    private JTextField customeNameLabel;
    private JTextField customerAddressLabel;
    private JTextField customerAddressField;
    private JTextField customerEmailLabel;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JTextField customerPhoneLabel;
    private JTextField customerInformationLabel;
    private JTextField paymentDetailsLabel;
    private JTextField totalAmountLabel;
    private JTextField totalAmountField;
    private JTextField paymentAmountLabel;
    private JTextField paymentAmountField;
    private JTextField paymentMethodLabel;
    private JTextField paymentMethodField;
    private JPanel panel2;
    private JTextField cartLabel;
    private JScrollPane scrollPane1;
    private JTable cartTable;
    private JTextField discountCodeLabel;
    private JTextField discountCodeField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void setupCartTable(Object[][] cartData) {
        // Set up cart table model with columns
        String[] columns = {"Item ID", "Item Name", "Category", "Unit Price", "VAT Type", "VAT Price", "Quantity", "Subtotal"};
        DefaultTableModel cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add the cart data
        for (Object[] row : cartData) {
            // Get the necessary values
            double vatPrice = Double.parseDouble(row[3].toString());
            int quantity = Integer.parseInt(row[6].toString());

            // Calculate VAT inclusive subtotal
            double vatInclusivePrice = vatPrice;
            double subtotal = vatInclusivePrice * quantity;

            // Update the subtotal in the row data
            row[7] = subtotal;

            cartTableModel.addRow(row);
        }

        cartTable.setModel(cartTableModel);

        // Set table properties
        cartTable.setShowGrid(false);
        cartTable.setRowHeight(40);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.setFocusable(false);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);

        // Custom cell renderer for alternating row colors and padding
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
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }

                // Format numeric values
                if (value != null) {
                    if (column == 3 || column == 5 || column == 7) { // Price columns
                        label.setText(String.format("%.2f", Double.parseDouble(value.toString())));
                    }
                }

                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < cartTable.getColumnCount(); i++) {
            cartTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Style the header
        JTableHeader header = cartTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder());
                label.setBackground(Color.decode("#6c39c1"));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return label;
            }
        });

        // Set column widths
        TableColumnModel columnModel = cartTable.getColumnModel();
        int[] columnWidths = {100, 200, 150, 120, 100, 120, 100, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
            column.setMinWidth(100);
        }

        // Enable table scrolling
        cartTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Calculate total width
        int totalWidth = Arrays.stream(columnWidths).sum();

        // Set preferred size for the table
        cartTable.setPreferredScrollableViewportSize(new Dimension(totalWidth, 400));

        // Set scroll pane properties
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Make the table fill the viewport
        cartTable.setFillsViewportHeight(true);

        // Set preferred size for the scroll pane
        scrollPane1.setPreferredSize(new Dimension(505, 400));

        // Modern scroll bar styling
        scrollPane1.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane1.setBorder(BorderFactory.createEmptyBorder());
    }
}

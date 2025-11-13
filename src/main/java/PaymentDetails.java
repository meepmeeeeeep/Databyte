// PaymentDetails.java

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class PaymentDetails extends JPanel {
    private final Sales sales;

    public PaymentDetails(String totalAmount, String paymentAmount, String paymentMethod, double change, Sales sales) {
        this.sales = sales;

        // Use Custom Background Images for Buttons
        //---- printButton ----
        Image printBg = new ImageIcon(getClass().getResource("/assets/images/printButton.png")).getImage();
        printButton = new ImageButton(printBg, "");

        initComponents();

        // Add Left-Padding to Text Fields
        //---- Payment Details ----
        totalAmountField.setBorder(BorderFactory.createCompoundBorder(
                totalAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentAmountField.setBorder(BorderFactory.createCompoundBorder(
                paymentAmountField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        changeField.setBorder(BorderFactory.createCompoundBorder(
                changeField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));
        paymentMethodField.setBorder(BorderFactory.createCompoundBorder(
                paymentMethodField.getBorder(),
                BorderFactory.createEmptyBorder(0, 10, 0, 10) // top, left, bottom, right
        ));

        // Set Text Fields Values
        totalAmountField.setText(totalAmount);
        paymentAmountField.setText(paymentAmount);
        paymentMethodField.setText(paymentMethod);
        // Change is only applicable for CASH payment method
        if (paymentMethodField.getText().equalsIgnoreCase("CASH")) {
            changeField.setText(String.format("%.2f", change));
        } else {
            changeField.setText("0.0");
        }
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Details Form
    }

    private void confirm(ActionEvent e) {
        sales.populateTableSales();

        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Details Form
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

                // Define fonts
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

                // Get the latest transaction ID from the database
                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    String sql = "SELECT th.transaction_id, th.date, th.customer_name, th.customer_phone, " +
                            "th.customer_address, th.payment_method, th.payment_amount, th.total_price, " +
                            "th.discount_code, th.employee_name " +
                            "FROM transaction_history th " +
                            "ORDER BY th.date DESC LIMIT 1";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String transactionId = rs.getString("transaction_id");
                        document.add(new Paragraph("Transaction ID: " + transactionId, normalFont));
                        document.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("date")), normalFont));
                        String employeeName = rs.getString("employee_name");
                        document.add(new Paragraph("Cashier Name: " + employeeName, normalFont));
                        document.add(new Paragraph("\nCustomer Information:", titleFont));
                        document.add(new Paragraph("Name: " + rs.getString("customer_name"), normalFont));

                        String phone = rs.getString("customer_phone");
                        if (phone != null && !phone.isEmpty()) {
                            document.add(new Paragraph("Contact: " + phone, normalFont));
                        }

                        String address = rs.getString("customer_address");
                        if (address != null && !address.isEmpty()) {
                            document.add(new Paragraph("Address: " + address, normalFont));
                        }

                        document.add(new Paragraph("\nOrder Details", titleFont));
                        document.add(new Paragraph("Payment Method: " + rs.getString("payment_method"), normalFont));
                        document.add(new Paragraph("", normalFont)); // Spacing

                        // Get cart items
                        sql = "SELECT * FROM cart_items WHERE transaction_id = ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, transactionId);
                        ResultSet itemsRs = stmt.executeQuery();

                        double originalTotal = 0.0;

                        while (itemsRs.next()) {
                            String itemId = itemsRs.getString("item_id");
                            String itemName = itemsRs.getString("item_name");
                            int quantity = itemsRs.getInt("quantity");
                            double unitPrice = itemsRs.getDouble("price");
                            double vatPrice = itemsRs.getDouble("vat_exclusive_price");

                            double subtotal = unitPrice * quantity;
                            originalTotal += subtotal;

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
                        double finalTotal = rs.getDouble("total_price");
                        double payment = rs.getDouble("payment_amount");
                        double change = payment - finalTotal;

                        String discountCode = rs.getString("discount_code");
                        if (discountCode != null && !discountCode.isEmpty()) {
                            double discountAmount = originalTotal - finalTotal;
                            Paragraph discountInfo = new Paragraph();
                            discountInfo.setAlignment(Element.ALIGN_RIGHT);
                            discountInfo.add(new Chunk(String.format("Original Total: %8.2f\n", originalTotal), normalFont));
                            discountInfo.add(new Chunk(String.format("Discount (%s): %8.2f\n", discountCode, discountAmount), normalFont));
                            document.add(discountInfo);
                        }

                        Paragraph totals = new Paragraph();
                        totals.setAlignment(Element.ALIGN_RIGHT);
                        totals.add(new Chunk(String.format("Total: %8.2f\n", finalTotal), titleFont));
                        totals.add(new Chunk(String.format("Paid: %8.2f\n", payment), normalFont));

                        // Display Change if Payment Method is CASH
                        String paymentMethod = rs.getString("payment_method");
                        if (paymentMethod.equalsIgnoreCase("CASH")) {
                            totals.add(new Chunk(String.format("Change: %8.2f\n", change), normalFont));
                        }

                        document.add(totals);
                    }
                }

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
            ex.printStackTrace();
        }
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        sidePanel = new JPanel();
        windowTitleContainer = new JPanel();
        dashboardLabel = new JTextField();
        panel1 = new JPanel();
        confirmButton = new JButton();
        cancelButton = new JButton();
        paymentDetailsLabel = new JTextField();
        totalAmountLabel = new JTextField();
        totalAmountField = new JTextField();
        paymentAmountLabel = new JTextField();
        paymentAmountField = new JTextField();
        paymentMethodLabel = new JTextField();
        paymentMethodField = new JTextField();
        changeLabel = new JTextField();
        changeField = new JTextField();

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
            dashboardLabel.setText("Payment Confirmation");
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
                        .addGap(19, 19, 19)
                        .addComponent(dashboardLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(15, Short.MAX_VALUE))
            );
        }

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0xfcf8ff));

            //---- confirmButton ----
            confirmButton.setText("CONFIRM");
            confirmButton.setBackground(new Color(0x6c39c1));
            confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            confirmButton.setForeground(new Color(0xfcf8ff));
            confirmButton.setFocusable(false);
            confirmButton.addActionListener(e -> confirm(e));

            //---- cancelButton ----
            cancelButton.setText("CANCEL");
            cancelButton.setBackground(new Color(0x6c39c1));
            cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelButton.setForeground(new Color(0xfcf8ff));
            cancelButton.setFocusable(false);
            cancelButton.addActionListener(e -> cancel(e));

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
            totalAmountField.setBackground(new Color(0xe8e7f4));
            totalAmountField.setEditable(false);
            totalAmountField.setFocusable(false);

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
            paymentMethodField.setBackground(new Color(0xe8e7f4));
            paymentMethodField.setEditable(false);
            paymentMethodField.setFocusable(false);

            //---- changeLabel ----
            changeLabel.setText("Change:");
            changeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
            changeLabel.setBackground(new Color(0xfcf8ff));
            changeLabel.setForeground(new Color(0x251779));
            changeLabel.setBorder(null);
            changeLabel.setFocusable(false);
            changeLabel.setEditable(false);

            //---- changeField ----
            changeField.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
            changeField.setBackground(new Color(0xe8e7f4));
            changeField.setEditable(false);
            changeField.setFocusable(false);

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

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentAmountField, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                            .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(changeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(changeField, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                            .addComponent(totalAmountField, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodField))
                        .addGap(20, 20, 20))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(changeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(changeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(24, Short.MAX_VALUE))
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
                .addComponent(sidePanel, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel sidePanel;
    private JPanel windowTitleContainer;
    private JTextField dashboardLabel;
    private JPanel panel1;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField paymentDetailsLabel;
    private JTextField totalAmountLabel;
    private JTextField totalAmountField;
    private JTextField paymentAmountLabel;
    private JTextField paymentAmountField;
    private JTextField paymentMethodLabel;
    private JTextField paymentMethodField;
    private JTextField changeLabel;
    private JTextField changeField;
    private JButton printButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

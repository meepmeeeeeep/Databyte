// PaymentDetails.java

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PaymentDetails extends JPanel {
    private final Sales sales;

    public PaymentDetails(String totalAmount, String paymentAmount, String paymentMethod, double change, Sales sales) {
        this.sales = sales;

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
        changeField.setText(String.format("%.2f", change));
    }

    private void cancel(ActionEvent e) {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Details Form
    }

    private void confirm(ActionEvent e) {
        sales.populateTableSales();

        SwingUtilities.getWindowAncestor(this).dispose(); // Close Payment Details Form
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
                        .addContainerGap(607, Short.MAX_VALUE))
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

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(changeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(changeField, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(31, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(paymentDetailsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(totalAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(paymentAmountLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(paymentAmountField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(paymentMethodLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(paymentMethodField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(changeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(changeField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                            .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

// LoginUI.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;

public class LoginUI extends JPanel {
    private final Image backgroundImage;

    // Initialize UI Components
    public LoginUI() {
        // Load image from resources classpath
        backgroundImage = new ImageIcon(getClass().getResource("/assets/images/loginBackground.png")).getImage();

        initComponents();
    }

    // Change background to Background Image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }

    //
    // Exit Button Event Listener Methods
    //
    private void exitWindow(ActionEvent e) {
        System.exit(0); // Exit Window
    }
    // Hover Effects - Mouse Enter
    private void exitButtonMouseEntered(MouseEvent e) {
        exitButton.setForeground(new Color(0xffffff));
    }
    // Hover Effects - Mouse Exit
    private void exitButtonMouseExited(MouseEvent e) {
        exitButton.setForeground(new Color(0x251779)); // Set to Default Color
    }

    //
    // Login Button Event Listener Methods
    //
    // Hover Effects - Mouse Enter
    private void loginButtonMouseEntered(MouseEvent e) {
        Image loginBg = new ImageIcon(getClass().getResource("/assets/images/loginButtonActive.png")).getImage();
        ((ImageButton) loginButton).setBackgroundImage(loginBg);
    }
    // Hover Effects - Mouse Exit
    private void loginButtonMouseExited(MouseEvent e) {
        Image loginBg = new ImageIcon(getClass().getResource("/assets/images/loginButton.png")).getImage();
        ((ImageButton) loginButton).setBackgroundImage(loginBg);
    }

    private void login(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        DBConnection.LoginResult result = DBConnection.validateLogin(username, password); // Returns User object if valid

        switch (result) {
            case SUCCESS:
                // Login successful: Open Dashboard
                SwingUtilities.getWindowAncestor(this).dispose();; // Close LoginUI

                JFrame frame = new JFrame("Dashboard");
                frame.setContentPane(new Dashboard());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                break;
            case INVALID_USERNAME:
                errorLabel.setText("Username does not exist.");
                break;
            case INVALID_PASSWORD:
                errorLabel.setText("Incorrect password.");
                break;
            case null:
                break;
            default:
                errorLabel.setText("Login failed. Please try again.");
                break;
        }
    }

    //
    // Initialize Components Methods
    //

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        welcomeLabel = new JLabel();
        scapeLabel = new JLabel();
        usernameLabel = new JLabel();
        usernameField = new JTextField();
        passwordLabel = new JLabel();
        passwordField = new JPasswordField();
        exitButton = new JButton();
        errorLabel = new JLabel();

        //======== this ========
        setBackground(new Color(0xfcf8ff));

        //---- welcomeLabel ----
        welcomeLabel.setText("Welcome to");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(0x897cce));
        welcomeLabel.setBackground(new Color(0xfcf8ff));

        //---- scapeLabel ----
        scapeLabel.setText("SCAPE");
        scapeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        scapeLabel.setForeground(new Color(0x251779));
        scapeLabel.setBackground(new Color(0xfcf8ff));

        //---- usernameLabel ----
        usernameLabel.setText("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(0x897cce));
        usernameLabel.setBackground(new Color(0xfcf8ff));

        //---- usernameField ----
        usernameField.setForeground(new Color(0x251779));
        usernameField.setBackground(new Color(0xfcf8ff));
        usernameField.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0x251779)));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        //---- passwordLabel ----
        passwordLabel.setText("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(0x897cce));
        passwordLabel.setBackground(new Color(0xfcf8ff));

        //---- passwordField ----
        passwordField.setForeground(new Color(0x251779));
        passwordField.setBackground(new Color(0xfcf8ff));
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0x251779)));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        //---- loginButton ----
        Image loginBg = new ImageIcon(getClass().getResource("/assets/images/loginButton.png")).getImage();
        loginButton = new ImageButton(loginBg, "LOGIN");
        loginButton.setForeground(new Color(0xfcf8ff));
        loginButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        loginButton.setBackground(new Color(0xaf53c4));
        loginButton.setBorderPainted(false);
        loginButton.setFocusable(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonMouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonMouseExited(e);
            }
        });
        loginButton.addActionListener(e -> login(e));

        //---- exitButton ----
        exitButton.setText("X");
        exitButton.setForeground(new Color(0x251779));
        exitButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> exitWindow(e));
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButtonMouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                exitButtonMouseExited(e);
            }
        });

        //---- errorLabel ----
        errorLabel.setText("");
        errorLabel.setForeground(new Color(0xcc0033));
        errorLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 950, Short.MAX_VALUE)
                    .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addGap(96, 96, 96)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                        .addComponent(usernameLabel)
                        .addComponent(welcomeLabel)
                        .addComponent(scapeLabel)
                        .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                        .addComponent(errorLabel))
                    .addContainerGap(624, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addGap(124, 124, 124)
                    .addComponent(welcomeLabel)
                    .addGap(0, 0, 0)
                    .addComponent(scapeLabel)
                    .addGap(47, 47, 47)
                    .addComponent(usernameLabel)
                    .addGap(0, 0, 0)
                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(passwordLabel)
                    .addGap(0, 0, 0)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(errorLabel)
                    .addGap(12, 12, 12)
                    .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(167, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel welcomeLabel;
    private JLabel scapeLabel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel errorLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

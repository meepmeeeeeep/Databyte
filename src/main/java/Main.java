// Main.java

import javax.swing.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login Form");
            frame.setUndecorated(true);         // Removes the title bar
            frame.setContentPane(new LoginUI());
            frame.pack();
            frame.setLocationRelativeTo(null);  // Centres the windows
            frame.setResizable(false);          // Disable window resizing
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

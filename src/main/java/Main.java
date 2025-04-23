// Main.java

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Inventory System");
            frame.setContentPane(new InventoryUI().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

// DBConnection.java

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBConnection {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_system"; // DB URL
    public static final String DB_USER = "root"; // DB username
    public static final String DB_PASSWORD = ""; // DB password

    public void initDatabase() {
        createUsersTable(); // Ensure users table exists at startup
        createInventoryTable(); // Ensure inventory table exists at startup
        createTransactionTable(); // Ensure sales table exists at startup
    }

    private void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL, "
                + "password VARCHAR(100) NOT NULL,"
                + "role ENUM('Admin', 'Customer', 'View-Only') NOT NULL"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);

            // Check if admin exists before inserting
            String checkAdminSQL = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdminSQL);
            rs.next();
            int count = rs.getInt(1);

            // Only insert if admin doesn't exist
            if (count == 0) {
                String insertAdminSQL = "INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'Admin')";
                stmt.executeUpdate(insertAdminSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createInventoryTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS inventory ("
                + "item_no INT AUTO_INCREMENT, "
                + "item_id VARCHAR(100) NOT NULL, "
                + "item_name VARCHAR(100) NOT NULL, "
                + "category VARCHAR(50), "
                + "quantity INT NOT NULL, "
                + "price DECIMAL(10, 2) NOT NULL, "
                + "PRIMARY KEY (item_id), "
                + "UNIQUE (item_no)"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTransactionTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transaction_history ("
                + "transaction_id VARCHAR(20) PRIMARY KEY, "
                + "item_id VARCHAR(100) NOT NULL, "
                + "item_name VARCHAR(100) NOT NULL, "
                + "category VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "price DECIMAL(10,2) NOT NULL, "
                + "total_price DECIMAL(10,2) NOT NULL, "
                + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                + "customer_name VARCHAR(100), "
                + "customer_address VARCHAR(255), "
                + "customer_email VARCHAR(100), "
                + "customer_phone VARCHAR(20), "
                + "payment_amount DECIMAL(10,2) NOT NULL, "
                + "payment_method VARCHAR(20) NOT NULL"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating transaction table: " + e.getMessage());
        }
    }

    public enum LoginResult {
        SUCCESS,
        INVALID_USERNAME,
        INVALID_PASSWORD
    }

    public static LoginResult validateLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return LoginResult.INVALID_USERNAME;
            }

            String correctPassword = rs.getString("password");
            if (!correctPassword.equals(password)) {
                return LoginResult.INVALID_PASSWORD;
            }

            return LoginResult.SUCCESS;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Generate Transaction ID
    public String generateTransactionID() {
        String datePrefix = new SimpleDateFormat("MMddyyyy").format(new Date()); // e.g., 05132025
        int nextNumber = 1;

        String sql = "SELECT transaction_id FROM transaction_history WHERE transaction_id LIKE ? ORDER BY transaction_id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, datePrefix + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("transaction_id");
                String numberPart = lastID.substring(8); // After MMDDYYYY
                nextNumber = Integer.parseInt(numberPart) + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error generating transaction ID: " + e.getMessage());
        }

        return datePrefix + String.format("%04d", nextNumber); // e.g., 051320250001
    }
}

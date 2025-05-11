// DBConnection.java

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    public static final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com/sql12778158"; // DB URL
    public static final String DB_USER = "sql12778158"; // DB username
    public static final String DB_PASSWORD = "bggRtELWar"; // DB password
    private Connection connection;

    public DBConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createUsersTable(); // Ensure users table exists at startup
            createInventoryTable(); // Ensure inventory table exists at startup
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL, "
                + "password VARCHAR(100) NOT NULL,"
                + "role ENUM('Admin', 'Customer', 'View-Only') NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
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

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();

        String query = "SELECT * FROM inventory";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int itemNo = rs.getInt("item_no");
                String itemId = rs.getString("item_id");
                String itemName = rs.getString("item_name");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                InventoryItem item = new InventoryItem(itemNo, itemId, itemName, category, quantity, price);
                items.add(item);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return items;
    }
}

// DatabaseManager.java

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS inventory (
                        item_id TEXT PRIMARY KEY,
                        category TEXT NOT NULL,
                        brand_name TEXT NOT NULL,
                        item_name TEXT NOT NULL,
                        item_price REAL NOT NULL,
                        item_stock INTEGER NOT NULL,
                        stock_status TEXT NOT NULL
                    );
                    """;
                stmt.execute(createTableSQL);
                System.out.println("Database and inventory table initialized.");
            }
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        String query = "SELECT * FROM inventory";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String category = rs.getString("category");
                String itemId = rs.getString("item_id");
                String brandName = rs.getString("brand_name");
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("item_price");
                int stockCount = rs.getInt("item_stock");

                InventoryItem item = new InventoryItem(category, itemId, brandName, itemName, price, stockCount);
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        return items;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

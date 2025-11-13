// DBConnection.java

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBConnection {
    private static final String APP_NAME = "Databyte";
    private static final String BACKUP_DIR = "database_backups";
    public static final String ROOT_DB_URL = "jdbc:mysql://localhost:3306"; // Root DB URL
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "inventory_system"; // Database name
    public static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_system"; // DB URL
    public static final String DB_USER = "root"; // DB username
    public static final String DB_PASSWORD = ""; // DB password

    public void initDatabase() {
        createDatabaseSchema(); // Create database schema if not exists

        createUsersTable(); // Ensure users table exists at startup
        createInventoryTable(); // Ensure inventory table exists at startup
        createTransactionTable(); // Ensure sales table exists at startup
        createCartTable(); // Ensure cart table exists at startup
        createResupplyHistoryTable(); // Ensure resupply history table exists at startup
        createDiscountCodesTable(); // Ensure discount codes table exists at startup
    }

    // Create Database Schema
    private void createDatabaseSchema() {
        try (Connection conn = DriverManager.getConnection(ROOT_DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Check if database exists
            ResultSet resultSet = conn.getMetaData().getCatalogs();
            boolean dbExists = false;

            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(DB_NAME)) {
                    dbExists = true;
                    break;
                }
            }

            if (!dbExists) {
                // Create database if it doesn't exist
                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
            }

            // Use the database
            stmt.executeUpdate("USE " + DB_NAME);

        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    // Create Users Table
    private void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "employee_name VARCHAR(255) NOT NULL, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(100) NOT NULL, "
                + "email VARCHAR(100), "
                + "contact_number VARCHAR(20), "
                + "role ENUM('ADMIN', 'MANAGER', 'CASHIER', 'STOCK CLERK') NOT NULL, "
                + "archived BOOLEAN DEFAULT FALSE"
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
                String insertAdminSQL = "INSERT INTO users (employee_name, username, password, email, role) "
                        + "VALUES ('Joe', 'admin', 'admin', 'admin@databyte.com', 'ADMIN')";
                stmt.executeUpdate(insertAdminSQL);
            }
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    // Create Inventory Table
    private void createInventoryTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS inventory ("
                + "item_no INT AUTO_INCREMENT, "
                + "item_id VARCHAR(100) NOT NULL, "
                + "item_name VARCHAR(100) NOT NULL, "
                + "category VARCHAR(50), "
                + "quantity INT NOT NULL, "
                + "price DECIMAL(10, 2) NOT NULL, "
                + "vat_type ENUM('VATABLE', 'VAT EXEMPT') DEFAULT 'VATABLE', "
                + "vat_inclusive_price DECIMAL(10, 2) GENERATED ALWAYS AS ("
                + "    CASE WHEN vat_type = 'VATABLE' THEN price * 1.12 ELSE price END"
                + ") STORED, "
                + "PRIMARY KEY (item_id), "
                + "UNIQUE (item_no), "
                + "archived BOOLEAN DEFAULT FALSE"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create Transaction History Table
    private void createTransactionTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transaction_history ("
                + "transaction_id VARCHAR(20) PRIMARY KEY, "
                + "total_price DECIMAL(10,2) NOT NULL, "
                + "vatable_amount DECIMAL(10,2) DEFAULT 0, "
                + "vat_amount DECIMAL(10,2) DEFAULT 0, "
                + "zero_rated_amount DECIMAL(10,2) DEFAULT 0, "
                + "vat_exempt_amount DECIMAL(10,2) DEFAULT 0, "
                + "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                + "customer_name VARCHAR(100), "
                + "customer_address VARCHAR(255), "
                + "customer_email VARCHAR(100), "
                + "customer_phone VARCHAR(20), "
                + "payment_amount DECIMAL(10,2) NOT NULL, "
                + "payment_method VARCHAR(20) NOT NULL, "
                + "discount_code VARCHAR(50)"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating transaction table: " + e.getMessage());
        }
    }

    // Create Cart Table
    private void createCartTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS cart_items ("
                + "id int(11) NOT NULL, "
                + "transaction_id varchar(50) DEFAULT NULL, "
                + "item_id varchar(50) DEFAULT NULL, "
                + "item_name varchar(100) DEFAULT NULL, "
                + "category varchar(50) DEFAULT NULL, "
                + "price decimal(10,2) DEFAULT NULL, "
                + "quantity int(11) DEFAULT NULL, "
                + "vat_type ENUM('VATABLE', 'ZERO-RATED', 'VAT EXEMPT') DEFAULT NULL, "
                + "vat_inclusive_price decimal(10,2) DEFAULT NULL"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating cart table: " + e.getMessage());
        }
    }

    private void createDiscountCodesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS discount_codes ( "
                + "code varchar(20) NOT NULL, "
                + "discount_percentage decimal(5,2) NOT NULL, "
                + "valid_from date NOT NULL, "
                + "valid_until date NOT NULL, "
                + "max_uses int(11) NOT NULL, "
                + "current_uses int(11) DEFAULT 0, "
                + "is_active tinyint(1) DEFAULT 1, "
                + "minimum_purchase decimal(10,2) NOT NULL DEFAULT 0.00, "
                + "archived BOOLEAN DEFAULT FALSE"
                + ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating discount codes table: " + e.getMessage());
        }
    }

    // Create Resupply History Table
    private void createResupplyHistoryTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS resupply_history ("
                + "resupply_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "item_id VARCHAR(20) NOT NULL, "
                + "item_name VARCHAR(100) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "supplier_name VARCHAR(100) NOT NULL, "
                + "supplier_address VARCHAR(200) DEFAULT NULL, "
                + "supplier_contact VARCHAR(50) DEFAULT NULL, "
                + "unit_cost DECIMAL(10,2) NOT NULL, "
                + "total_cost DECIMAL(10,2) NOT NULL, "
                + "resupply_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating resupply history table: " + e.getMessage());
        }
    }

    public enum LoginResult {
        SUCCESS,
        INVALID_USERNAME,
        INVALID_PASSWORD
    }

    public static LoginResult validateLogin(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ? AND archived = FALSE");
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
                String numberPart = lastID.substring(datePrefix.length()); // After MMDDYYYY
                nextNumber = Integer.parseInt(numberPart) + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error generating transaction ID: " + e.getMessage());
        }

        return datePrefix + String.format("%03d", nextNumber); // e.g., 05132025001
    }

    public static String getUserRole(String username) {
        String role = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return role;
    }

    public static String getEmployeeName(String username) {
        String employee_name = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT employee_name FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                employee_name = rs.getString("employee_name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return employee_name;
    }

    public static String getBackupDirectory() throws IOException {
        String appData = System.getenv("APPDATA");
        Path backupPath = Paths.get(appData, APP_NAME, BACKUP_DIR);

        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }

        return backupPath.toString();
    }

    public static void backupDatabase(String backupPath) throws IOException, InterruptedException {
        StringBuilder backup = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // Get all tables
            ResultSet tables = metaData.getTables(DB_NAME, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                // Get table structure first
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
                    if (rs.next()) {
                        backup.append(rs.getString(2)).append(";\n\n");
                    }
                }

                // Get table data excluding generated columns
                try (Statement stmt = conn.createStatement()) {
                    // Get column info to exclude generated columns
                    ResultSet columns = metaData.getColumns(DB_NAME, null, tableName, null);
                    List<String> normalColumns = new ArrayList<>();

                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String isGenerated = columns.getString("IS_GENERATEDCOLUMN");
                        if (!"YES".equals(isGenerated)) {
                            normalColumns.add(columnName);
                        }
                    }

                    if (!normalColumns.isEmpty()) {
                        // Build column list for SELECT
                        String columnList = String.join(", ", normalColumns);
                        ResultSet rs = stmt.executeQuery("SELECT " + columnList + " FROM " + tableName);
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();

                        while (rs.next()) {
                            backup.append("INSERT INTO ").append(tableName)
                                    .append(" (").append(columnList).append(") VALUES (");

                            for (int i = 1; i <= columnCount; i++) {
                                if (i > 1) backup.append(", ");
                                Object value = rs.getObject(i);
                                if (value == null) {
                                    backup.append("NULL");
                                } else if (value instanceof String || value instanceof Date ||
                                        value instanceof Timestamp || value instanceof Time) {
                                    backup.append("'").append(value.toString().replace("'", "\\'")).append("'");
                                } else {
                                    backup.append(value.toString());
                                }
                            }
                            backup.append(");\n");
                        }
                        backup.append("\n");
                    }
                }
            }

            // Write to file
            Path backupFilePath = Paths.get(backupPath);
            Files.createDirectories(backupFilePath.getParent());
            Files.write(backupFilePath, backup.toString().getBytes());

        } catch (SQLException e) {
            System.out.println("Error during backup: " + e.getMessage());
        }
    }

    public static void restoreDatabase(String backupPath) throws IOException, InterruptedException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // First, disable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Get all existing tables and drop them
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(DB_NAME, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                stmt.execute("DROP TABLE IF EXISTS " + tableName);
            }

            // Read and execute SQL statements from backup
            String[] statements = new String(Files.readAllBytes(Paths.get(backupPath)))
                    .split(";");

            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }

            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        catch (SQLException e) {
            System.out.println("Error during restore: " + e.getMessage());
        }
    }
}

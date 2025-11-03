import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class SalesAnalysisChart {
    private String currentPeriod;

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public SalesAnalysisChart() {
        ChartFactory.setChartTheme(new ModernChartTheme());
    }

    public JFreeChart createRevenueLineChart(String period) {
        this.currentPeriod = period;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Define arrays for all possible values
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        String query;
        switch (period) {
            case "Yearly":
                // Get current year and ensure 5 years of data
                int currentYear = java.time.Year.now().getValue();
                query = "SELECT YEAR(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history " +
                        "GROUP BY YEAR(date) " +
                        "ORDER BY YEAR(date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    // Create map to store existing data
                    java.util.Map<Integer, Double> yearData = new java.util.HashMap<>();
                    while (rs.next()) {
                        yearData.put(rs.getInt("period_label"), rs.getDouble("total_revenue"));
                    }

                    // Ensure 5 years of data
                    for (int year = currentYear - 4; year <= currentYear; year++) {
                        dataset.addValue(yearData.getOrDefault(year, 0.0), "Revenue", String.valueOf(year));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "Monthly":
                query = "SELECT MONTHNAME(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history " +
                        "GROUP BY MONTH(date), MONTHNAME(date) " +
                        "ORDER BY MONTH(date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    java.util.Map<String, Double> monthData = new java.util.HashMap<>();
                    while (rs.next()) {
                        monthData.put(rs.getString("period_label"), rs.getDouble("total_revenue"));
                    }

                    // Add all months
                    for (String month : months) {
                        dataset.addValue(monthData.getOrDefault(month, 0.0), "Revenue", month);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            default: // Weekly
                query = "SELECT DAYNAME(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history " +
                        "WHERE YEARWEEK(date, 0) = YEARWEEK(CURRENT_DATE(), 0) " +
                        "GROUP BY DAYOFWEEK(date), DAYNAME(date) " +
                        "ORDER BY DAYOFWEEK(date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    java.util.Map<String, Double> dayData = new java.util.HashMap<>();
                    while (rs.next()) {
                        dayData.put(rs.getString("period_label"), rs.getDouble("total_revenue"));
                    }

                    // Add all days
                    for (String day : daysOfWeek) {
                        dataset.addValue(dayData.getOrDefault(day, 0.0), "Revenue", day);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Revenue Over Time",
                period,
                "Revenue",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Style the chart
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        // Configure line renderer
        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer =
                (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Adjust line thickness
        renderer.setSeriesPaint(0, new Color(108, 57, 193));

        // Ensure the range axis starts at 0
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);

        // Rotate category labels for better readability
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        return chart;
    }

    public JFreeChart createCategoryComparisonChart() {
        JFreeChart chart = null;

        try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            String query = "SELECT i.category, SUM(c.quantity * c.price) as total_sales " +
                    "FROM cart_items c " +
                    "JOIN inventory i ON c.item_id = i.item_id " +
                    "GROUP BY i.category " +
                    "ORDER BY total_sales DESC";

            // Create separate series for each category instead of using a single "Sales" series
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            int seriesCount = 0;
            while (rs.next()) {
                String category = rs.getString("category");
                dataset.addValue(rs.getDouble("total_sales"),
                        category, // Use category as series name instead of "Sales"
                        category);
                seriesCount++;
            }

            chart = ChartFactory.createBarChart(
                    "Sales by Category",
                    "Category",
                    "Sales Amount",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(new Color(230, 230, 230));

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setShadowVisible(false);
            renderer.setMaximumBarWidth(0.5);
            renderer.setItemMargin(0.0);

            // Define colors for bars
            Color[] colors = {
                    new Color(108, 57, 193),  // Purple
                    new Color(220, 53, 69),   // Red
                    new Color(40, 167, 69),   // Green
                    new Color(255, 193, 7),   // Yellow
                    new Color(23, 162, 184),  // Cyan
                    new Color(108, 117, 125), // Gray
                    new Color(0, 123, 255)    // Blue
            };

            // Set different colors for each series
            for (int i = 0; i < seriesCount; i++) {
                renderer.setSeriesPaint(i, colors[i % colors.length]);
            }

            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

            chart.setBorderVisible(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chart;
    }

    public JFreeChart createCostLineChart(String period) {
        this.currentPeriod = period;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        String query;
        switch (period) {
            case "Yearly":
                int currentYear = java.time.Year.now().getValue();
                query = "SELECT YEAR(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history " +
                        "GROUP BY YEAR(resupply_date) " +
                        "ORDER BY YEAR(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    java.util.Map<Integer, Double> yearData = new java.util.HashMap<>();
                    while (rs.next()) {
                        yearData.put(rs.getInt("period_label"), rs.getDouble("total_cost"));
                    }

                    for (int year = currentYear - 4; year <= currentYear; year++) {
                        dataset.addValue(yearData.getOrDefault(year, 0.0), "Cost", String.valueOf(year));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "Monthly":
                query = "SELECT MONTHNAME(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history " +
                        "GROUP BY MONTH(resupply_date), MONTHNAME(resupply_date) " +
                        "ORDER BY MONTH(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    java.util.Map<String, Double> monthData = new java.util.HashMap<>();
                    while (rs.next()) {
                        monthData.put(rs.getString("period_label"), rs.getDouble("total_cost"));
                    }

                    for (String month : months) {
                        dataset.addValue(monthData.getOrDefault(month, 0.0), "Cost", month);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            default: // Weekly
                query = "SELECT DAYNAME(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history " +
                        "WHERE YEARWEEK(resupply_date, 0) = YEARWEEK(CURRENT_DATE(), 0) " +
                        "GROUP BY DAYOFWEEK(resupply_date), DAYNAME(resupply_date) " +
                        "ORDER BY DAYOFWEEK(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    java.util.Map<String, Double> dayData = new java.util.HashMap<>();
                    while (rs.next()) {
                        dayData.put(rs.getString("period_label"), rs.getDouble("total_cost"));
                    }

                    for (String day : daysOfWeek) {
                        dataset.addValue(dayData.getOrDefault(day, 0.0), "Cost", day);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        return styleLineChart(dataset, "Cost Over Time", period, new Color(220, 53, 69));
    }

    public JFreeChart createProfitLineChart(String period) {
        this.currentPeriod = period;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        String revenueQuery, costQuery;
        switch (period) {
            case "Yearly":
                int currentYear = java.time.Year.now().getValue();
                revenueQuery = "SELECT YEAR(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history GROUP BY YEAR(date)";
                costQuery = "SELECT YEAR(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history GROUP BY YEAR(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    java.util.Map<Integer, Double> profitMap = new java.util.HashMap<>();

                    // Get revenue data
                    PreparedStatement revenueStmt = conn.prepareStatement(revenueQuery);
                    ResultSet revenueRs = revenueStmt.executeQuery();
                    while (revenueRs.next()) {
                        int year = revenueRs.getInt("period_label");
                        profitMap.put(year, revenueRs.getDouble("total_revenue"));
                    }

                    // Subtract costs
                    PreparedStatement costStmt = conn.prepareStatement(costQuery);
                    ResultSet costRs = costStmt.executeQuery();
                    while (costRs.next()) {
                        int year = costRs.getInt("period_label");
                        double currentProfit = profitMap.getOrDefault(year, 0.0);
                        profitMap.put(year, currentProfit - costRs.getDouble("total_cost"));
                    }

                    // Add data ensuring 5 years
                    for (int year = currentYear - 4; year <= currentYear; year++) {
                        dataset.addValue(profitMap.getOrDefault(year, 0.0), "Profit", String.valueOf(year));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "Monthly":
                revenueQuery = "SELECT MONTHNAME(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history GROUP BY MONTH(date), MONTHNAME(date)";
                costQuery = "SELECT MONTHNAME(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history GROUP BY MONTH(resupply_date), MONTHNAME(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    java.util.Map<String, Double> profitMap = new java.util.HashMap<>();

                    // Get revenue data
                    PreparedStatement revenueStmt = conn.prepareStatement(revenueQuery);
                    ResultSet revenueRs = revenueStmt.executeQuery();
                    while (revenueRs.next()) {
                        String month = revenueRs.getString("period_label");
                        profitMap.put(month, revenueRs.getDouble("total_revenue"));
                    }

                    // Subtract costs
                    PreparedStatement costStmt = conn.prepareStatement(costQuery);
                    ResultSet costRs = costStmt.executeQuery();
                    while (costRs.next()) {
                        String month = costRs.getString("period_label");
                        double currentProfit = profitMap.getOrDefault(month, 0.0);
                        profitMap.put(month, currentProfit - costRs.getDouble("total_cost"));
                    }

                    // Add data for all months
                    for (String month : months) {
                        dataset.addValue(profitMap.getOrDefault(month, 0.0), "Profit", month);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            default: // Weekly
                revenueQuery = "SELECT DAYNAME(date) as period_label, COALESCE(SUM(total_price), 0) as total_revenue " +
                        "FROM transaction_history WHERE YEARWEEK(date, 0) = YEARWEEK(CURRENT_DATE(), 0) " +
                        "GROUP BY DAYOFWEEK(date), DAYNAME(date)";
                costQuery = "SELECT DAYNAME(resupply_date) as period_label, COALESCE(SUM(total_cost), 0) as total_cost " +
                        "FROM resupply_history WHERE YEARWEEK(resupply_date, 0) = YEARWEEK(CURRENT_DATE(), 0) " +
                        "GROUP BY DAYOFWEEK(resupply_date), DAYNAME(resupply_date)";

                try (Connection conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DB_USER, DBConnection.DB_PASSWORD)) {
                    java.util.Map<String, Double> profitMap = new java.util.HashMap<>();

                    // Get revenue data
                    PreparedStatement revenueStmt = conn.prepareStatement(revenueQuery);
                    ResultSet revenueRs = revenueStmt.executeQuery();
                    while (revenueRs.next()) {
                        String day = revenueRs.getString("period_label");
                        profitMap.put(day, revenueRs.getDouble("total_revenue"));
                    }

                    // Subtract costs
                    PreparedStatement costStmt = conn.prepareStatement(costQuery);
                    ResultSet costRs = costStmt.executeQuery();
                    while (costRs.next()) {
                        String day = costRs.getString("period_label");
                        double currentProfit = profitMap.getOrDefault(day, 0.0);
                        profitMap.put(day, currentProfit - costRs.getDouble("total_cost"));
                    }

                    // Add data for all days
                    for (String day : daysOfWeek) {
                        dataset.addValue(profitMap.getOrDefault(day, 0.0), "Profit", day);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        return styleLineChart(dataset, "Profit Over Time", period, new Color(40, 167, 69));
    }

    private JFreeChart styleLineChart(DefaultCategoryDataset dataset, String title, String period, Color lineColor) {
        JFreeChart chart = ChartFactory.createLineChart(
                title, period, "Amount", dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer =
                (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, lineColor);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        return chart;
    }
}
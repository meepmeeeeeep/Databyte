// InventoryItem.java
public class InventoryItem {
    public String category, itemId, brandName, itemName;
    public double price;
    public int stockCount;
    public String stockStatus;

    public InventoryItem(String category, String itemId, String brandName, String itemName, double price, int stockCount) {
        this.category = category;
        this.itemId = itemId;
        this.brandName = brandName;
        this.itemName = itemName;
        this.price = price;
        this.stockCount = stockCount;
        this.stockStatus = calculateStockStatus(stockCount);
    }

    private String calculateStockStatus(int count) {
        if (count == 0) return "Out of Stock";
        else if (count <= 10) return "Low Stock";
        else return "In Stock";
    }
}

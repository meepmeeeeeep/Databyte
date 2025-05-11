public class InventoryItem {
    private int itemNo;
    private String itemId;
    private String itemName;
    private String category;
    private int quantity;
    private double price;

    public InventoryItem(int itemNo, String itemId, String itemName, String category, int quantity, double price) {
        this.itemNo = itemNo;
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public int getItemNo() {
        return itemNo;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

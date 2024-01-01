package model;


public class OrderItem {
	private int orderID;
    private MenuItem menuItem; // Changed to MenuItem type
    private int quantity;

    public OrderItem(int orderID, MenuItem menuItem, int quantity) {
        this.orderID = orderID;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

	public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public int getMenuItemID() {
        return menuItem != null ? menuItem.getMenuItemID() : 0;
    }

    public String getMenuItemName() {
        return menuItem != null ? menuItem.getMenuItemName() : null;
    }

    public double getMenuItemPrice() {
        return menuItem != null ? menuItem.getMenuItemPrice() : 0.0;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

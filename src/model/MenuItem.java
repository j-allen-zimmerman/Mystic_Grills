package model;

public class MenuItem {
    private int menuItemID;
    private String menuItemName;
    private String menuItemDescription;
    private double menuItemPrice;


    public MenuItem(int menuItemID, String menuItemName, String menuItemDescription, double menuItemPrice) {
		super();
		this.menuItemID = menuItemID;
		this.menuItemName = menuItemName;
		this.menuItemDescription = menuItemDescription;
		this.menuItemPrice = menuItemPrice;
	}

	public MenuItem(String menuItemName, String menuItemDescription, double menuItemPrice) {
        this.menuItemName = menuItemName;
        this.menuItemDescription = menuItemDescription;
        this.menuItemPrice = menuItemPrice;
    }
	
	public MenuItem() {
        // You can initialize default values or leave it empty depending on your needs
    }

    // Getters and Setters
    public int getMenuItemID() {
        return menuItemID;
    }

    public void setMenuItemID(int menuItemID) {
        this.menuItemID = menuItemID;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemDescription() {
        return menuItemDescription;
    }

    public void setMenuItemDescription(String menuItemDescription) {
        this.menuItemDescription = menuItemDescription;
    }

    public double getMenuItemPrice() {
        return menuItemPrice;
    }

    public void setMenuItemPrice(double menuItemPrice) {
        this.menuItemPrice = menuItemPrice;
    }
}

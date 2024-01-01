	package controller;
	
	import model.MenuItem;
	
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.function.BiConsumer;
	
	import database.Connect;
	
	public class MenuItemController {
	
		public static boolean addMenuItem(MenuItem menuItem, BiConsumer<String, String> showAlert) {
	        if (validateMenuItem(menuItem, showAlert)) {
	            try (Connection connection = Connect.getInstance().getConnection()) {
	                String query = "INSERT INTO menuitems (MenuItemName, MenuItemDescription, MenuItemPrice) VALUES (?, ?, ?)";
	                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	                    preparedStatement.setString(1, menuItem.getMenuItemName());
	                    preparedStatement.setString(2, menuItem.getMenuItemDescription());
	                    preparedStatement.setDouble(3, menuItem.getMenuItemPrice());
	                    preparedStatement.executeUpdate();
	                }
	                showAlert.accept("Success", "Menu Item added successfully!");
	                return true; // Return true to indicate success
	            } catch (SQLException e) {
	                handleSQLException(showAlert, "Error adding Menu Item to the database", e);
	            }
	        }
	        return false;
	    }

	    public static void updateMenuItem(MenuItem menuItem, BiConsumer<String, String> showAlert) {
	        if (validateMenuItem(menuItem, showAlert)) {
	            try (Connection connection = Connect.getInstance().getConnection()) {
	                String query = "UPDATE menuitems SET MenuItemName=?, MenuItemDescription=?, MenuItemPrice=? WHERE MenuItemID=?";
	                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	                    preparedStatement.setString(1, menuItem.getMenuItemName());
	                    preparedStatement.setString(2, menuItem.getMenuItemDescription());
	                    preparedStatement.setDouble(3, menuItem.getMenuItemPrice());
	                    preparedStatement.setInt(4, menuItem.getMenuItemID());
	                    preparedStatement.executeUpdate();
	                }
	                showAlert.accept("Success", "Menu Item updated successfully!");
	            } catch (SQLException e) {
	                handleSQLException(showAlert, "Error updating Menu Item in the database", e);
	            }
	        }
	    }
	
	    // Method to delete a Menu Item
	    public static void deleteMenuItem(int menuItemID, BiConsumer<String, String> showAlert) {
	        try (Connection connection = Connect.getInstance().getConnection()) {
	            String query = "DELETE FROM menuitems WHERE MenuItemID=?";
	            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	                preparedStatement.setInt(1, menuItemID);
	                preparedStatement.executeUpdate();
	            }
	            showAlert.accept("Success", "Menu Item deleted successfully!");
	        } catch (SQLException e) {
	            handleSQLException(showAlert, "Error deleting Menu Item from the database", e);
	        }
	    }
	
	    // Method to retrieve all Menu Items
	    public static List<MenuItem> getAllMenuItems() {
	        List<MenuItem> menuItems = new ArrayList<>();
	        try (Connection connection = Connect.getInstance().getConnection()) {
	            String query = "SELECT * FROM menuitems";
	            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
	                 ResultSet resultSet = preparedStatement.executeQuery()) {
	                while (resultSet.next()) {
	                    MenuItem menuItem = new MenuItem();
	                    menuItem.setMenuItemID(resultSet.getInt("MenuItemID"));
	                    menuItem.setMenuItemName(resultSet.getString("MenuItemName"));
	                    menuItem.setMenuItemDescription(resultSet.getString("MenuItemDescription"));
	                    menuItem.setMenuItemPrice(resultSet.getDouble("MenuItemPrice"));
	                    menuItems.add(menuItem);
	                }
	            }
	        } catch (SQLException e) {
	            handleSQLException(null, "Error retrieving Menu Items from the database", e);
	        }
	        return menuItems;
	    }
	
	    // Validation method for Menu Item
	    private static boolean validateMenuItem(MenuItem menuItem, BiConsumer<String, String> showAlert) {
	        if (menuItem.getMenuItemName().isEmpty()) {
	            showAlert.accept("Error", "Menu Item Name cannot be empty.");
	            return false;
	        }
	
	        if (!isMenuItemNameUnique(menuItem.getMenuItemID(), menuItem.getMenuItemName())) {
	            showAlert.accept("Error", "Menu Item Name must be unique.");
	            return false;
	        }
	
	        if (menuItem.getMenuItemDescription().length() <= 10) {
	            showAlert.accept("Error", "Menu Item Description must be more than 10 characters.");
	            return false;
	        }
	
	        if (menuItem.getMenuItemPrice() < 2.5) {
	            showAlert.accept("Error", "Menu Item Price must be greater than or equal to 2.5.");
	            return false;
	        }
	
	        return true;
	    }
	
	    // Method to check if Menu Item Name is unique
	    private static boolean isMenuItemNameUnique(int menuItemID, String menuItemName) {
	        try (Connection connection = Connect.getInstance().getConnection()) {
	            String query = "SELECT COUNT(*) FROM menuitems WHERE MenuItemName = ? AND MenuItemID != ?";
	            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	                preparedStatement.setString(1, menuItemName);
	                preparedStatement.setInt(2, menuItemID);
	                try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                    if (resultSet.next()) {
	                        int count = resultSet.getInt(1);
	                        return count == 0;
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            handleSQLException(null, "Error checking Menu Item Name uniqueness", e);
	        }
	        return false;
	    }
	    
	    public static MenuItem getMenuItemById(int menuItemId) {
	        String query = "SELECT * FROM menuitems WHERE MenuItemID = ?";
	        Connect con = Connect.getInstance();

	        try (PreparedStatement ps = con.getConnection().prepareStatement(query)) {
	            ps.setInt(1, menuItemId);
	            ResultSet resultSet = ps.executeQuery();

	            if (resultSet.next()) {
	                // Retrieve details from the result set
	                String menuItemName = resultSet.getString("menuItemName");
	                String menuItemDescription = resultSet.getString("menuItemDescription");
	                double menuItemPrice = resultSet.getDouble("menuItemPrice");

	                // Create and return a MenuItem object
	                return new MenuItem(menuItemId, menuItemName, menuItemDescription, menuItemPrice);
	            } else {
	                // No menu item found with the specified ID
	                return null;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    private static void handleSQLException(BiConsumer<String, String> showAlert, String message, SQLException e) {
	        if (showAlert != null) {
	            showAlert.accept("Error", message + ": " + e.getMessage());
	        } else {
	            e.printStackTrace(); // or log the exception using a logger
	        }
	    }
	
	}

package controller;

import database.Connect;
import model.MenuItem;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemController {
	
	public static void createOrderItem(int orderId, int menuItemId, int quantity) {
	    String query = "INSERT INTO orderItem (orderId, menuItem, quantity) VALUES (?, ?, ?)";
	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        
	        ps.setInt(1, orderId);
	        ps.setInt(2, menuItemId);
	        ps.setInt(3, quantity);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static ArrayList<OrderItem> getAllOrderItems(int userId) {
	    ArrayList<OrderItem> orderList = new ArrayList<>();

	    Connect con = Connect.getInstance();
	    String query = "SELECT oi.orderId, oi.menuitem, oi.quantity, mi.* " +
	                   "FROM OrderItem oi " +
	                   "JOIN Orders o ON oi.orderId = o.orderId " +
	                   "JOIN MenuItems mi ON oi.menuitem = mi.menuItemId " +
	                   "WHERE o.orderUser = ?";

	    try (PreparedStatement preparedStatement = con.getConnection().prepareStatement(query)) {
	        preparedStatement.setInt(1, userId);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            int orderId = resultSet.getInt("orderId");
	            int quantity = resultSet.getInt("quantity");

	            // Construct MenuItem object
	            int menuItemId = resultSet.getInt("menuItemId");
	            String menuItemName = resultSet.getString("menuItemName");
	            String menuItemDescription = resultSet.getString("menuItemDescription");
	            double menuItemPrice = resultSet.getDouble("menuItemPrice");
	            MenuItem menuItem = new MenuItem(menuItemId, menuItemName, menuItemDescription, menuItemPrice);

	            // Construct OrderItem object with MenuItem
	            orderList.add(new OrderItem(orderId, menuItem, quantity));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return orderList;
	}

	
	public static OrderItem getOrderItemById(int orderItemId) {
	    String query = "SELECT oi.orderId, oi.menuItemId, oi.quantity, mi.* " +
	                   "FROM OrderItem oi " +
	                   "JOIN MenuItems mi ON oi.menuItemId = mi.menuItemId " +
	                   "WHERE oi.orderItemId = ?";

	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, orderItemId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                int orderId = rs.getInt("orderId");
	                int quantity = rs.getInt("quantity");

	                // Construct MenuItem object
	                int menuItemId = rs.getInt("menuItemId");
	                String menuItemName = rs.getString("menuItemName");
	                String menuItemDescription = rs.getString("menuItemDescription");
	                double menuItemPrice = rs.getDouble("menuItemPrice");
	                MenuItem menuItem = new MenuItem(menuItemId, menuItemName, menuItemDescription, menuItemPrice);

	                // Construct and return OrderItem object with MenuItem
	                return new OrderItem(orderId, menuItem, quantity);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	

	public static List<OrderItem> getOrderItemsByOrderId(int orderId) {
	    List<OrderItem> orderItems = new ArrayList<>();
	    String query = "SELECT oi.quantity, m.MenuItemID, m.MenuItemName, m.MenuItemPrice " +
	                   "FROM OrderItem oi JOIN MenuItems m ON oi.menuItem = m.MenuItemID " +
	                   "WHERE oi.orderId = ?";

	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, orderId);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            int menuItemId = rs.getInt("MenuItemID");
	            String menuItemName = rs.getString("MenuItemName");
	            double menuItemPrice = rs.getDouble("MenuItemPrice");
	            int quantity = rs.getInt("quantity");

	            // Create a MenuItem object
	            MenuItem menuItem = new MenuItem(menuItemId, menuItemName, null, menuItemPrice);

	            // Create an OrderItem object with the MenuItem
	            OrderItem orderItem = new OrderItem(orderId, menuItem, quantity);

	            orderItems.add(orderItem);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return orderItems;
	}
	
	public static void addNewItemToOrder(int orderId, int menuItemId, int quantity) {
        String checkQuery = "SELECT quantity FROM OrderItem WHERE orderId = ? AND menuItem = ?";
        String insertQuery = "INSERT INTO OrderItem (orderId, menuItem, quantity) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE OrderItem SET quantity = quantity + ? WHERE orderId = ? AND menuItem = ?";

        try (Connection connection = Connect.getInstance().getConnection()) {
            try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
                checkPs.setInt(1, orderId);
                checkPs.setInt(2, menuItemId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    // Item exists, update quantity
                    try (PreparedStatement updatePs = connection.prepareStatement(updateQuery)) {
                        updatePs.setInt(1, quantity);
                        updatePs.setInt(2, orderId);
                        updatePs.setInt(3, menuItemId);
                        updatePs.executeUpdate();
                    }
                } else {
                    // Item does not exist, insert new item
                    try (PreparedStatement insertPs = connection.prepareStatement(insertQuery)) {
                        insertPs.setInt(1, orderId);
                        insertPs.setInt(2, menuItemId);
                        insertPs.setInt(3, quantity);
                        insertPs.executeUpdate();
                    }
                }
            }
            OrderController.updateOrderTotal(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


	public static void updateOrderItemQuantity(OrderItem item, int newQuantity) {
	    String query = "UPDATE OrderItem SET quantity = ? WHERE orderId = ? AND menuItem = ?";
	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, newQuantity);
	        ps.setInt(2, item.getOrderID());
	        ps.setInt(3, item.getMenuItemID());
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public static void deleteOrderItem(int orderId, int menuItemId) {
        String query = "DELETE FROM OrderItem WHERE orderId = ? AND menuItem = ?";
        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

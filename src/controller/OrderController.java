package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.Connect;
import model.Order;

public class OrderController {
	public static int createOrder(int orderUser, String orderStatus, double orderTotal) {
	    String query = "INSERT INTO orders (orderUser, orderStatus, orderDate, orderTotal) VALUES (?, ?, ?, ?)";
	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        
	        ps.setInt(1, orderUser);
	        ps.setString(2, orderStatus);
	        ps.setString(3, getCurrentDate());
	        ps.setDouble(4, orderTotal);
	        ps.executeUpdate();

	        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; 
	}
	
	public static List<Order> getPendingOrders() {
        List<Order> pendingOrders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE orderStatus = 'Pending'";

        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                int orderUser = rs.getInt("orderUser");
                String orderStatus = rs.getString("orderStatus");
                String orderDate = rs.getString("orderDate");
                double orderTotal = rs.getDouble("orderTotal");
                pendingOrders.add(new Order(orderId, orderUser, orderStatus, orderDate, orderTotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingOrders;
    }
	
	public static List<Order> getPreparedOrders() {
        List<Order> preparedOrders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE orderStatus = 'Prepared'";

        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                int orderUser = rs.getInt("orderUser");
                String orderStatus = rs.getString("orderStatus");
                String orderDate = rs.getString("orderDate");
                double orderTotal = rs.getDouble("orderTotal");
                preparedOrders.add(new Order(orderId, orderUser, orderStatus, orderDate, orderTotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedOrders;
    }
	
	public static List<Order> getServedOrders() {
        List<Order> preparedOrders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE orderStatus = 'Served'";

        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                int orderUser = rs.getInt("orderUser");
                String orderStatus = rs.getString("orderStatus");
                String orderDate = rs.getString("orderDate");
                double orderTotal = rs.getDouble("orderTotal");
                preparedOrders.add(new Order(orderId, orderUser, orderStatus, orderDate, orderTotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedOrders;
    }
	
	 public static double getOrderTotal(int orderId) {
	        String query = "SELECT SUM(oi.quantity * m.MenuItemPrice) AS total " +
	                       "FROM OrderItem oi JOIN MenuItems m ON oi.menuItem = m.MenuItemID " +
	                       "WHERE oi.orderId = ?";
	        try (Connection connection = Connect.getInstance().getConnection();
	             PreparedStatement ps = connection.prepareStatement(query)) {
	            
	            ps.setInt(1, orderId);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return rs.getDouble("total");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0.0;
	    }
	
	
	public static void updateOrderTotal(int orderId) {
        String query = "UPDATE Orders "
                     + "SET orderTotal = (SELECT SUM(oi.quantity * m.MenuItemPrice) "
                     + "                   FROM OrderItem oi "
                     + "                   JOIN MenuItems m ON oi.menuItem = m.MenuItemID "
                     + "                   WHERE oi.orderId = ?)"
        			 + "WHERE orderId = ?";
        Connect connection = Connect.getInstance();
        PreparedStatement ps = connection.prepareStatement(query);
        try {
            ps.setInt(1, orderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
	
	public static void updateOrderItems(int orderId) throws SQLException {
        String query = "UPDATE OrderItem SET orderId = ? WHERE orderId IS NULL";
        Connection connection = Connect.getInstance().getConnection();
        PreparedStatement ps =  connection.prepareStatement(query);
        try {

            ps.setInt(1, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
	
	public static void updateOrderStatus(int orderId, String newStatus) {
        String query = "UPDATE Orders SET orderStatus = ? WHERE orderId = ?";
        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public static List<Order> getOrdersByUserId(int userId) {
	    List<Order> orders = new ArrayList<>();
	    String query = "SELECT * FROM Orders WHERE orderUser = ?";
	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            int orderId = rs.getInt("orderId");
	            String orderDate = rs.getString("orderDate");
	            double totalAmount = rs.getDouble("orderTotal");
	            String status = rs.getString("orderStatus");
	            orders.add(new Order(orderId, userId, status, orderDate, totalAmount));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return orders;
	}
	
	public static void updateOrderTotalInDatabase(int orderId) {
	    String query = "UPDATE Orders SET orderTotal = (SELECT SUM(oi.quantity * m.MenuItemPrice) FROM OrderItem oi JOIN MenuItems m ON oi.menuItem = m.MenuItemID WHERE oi.orderId = ?) WHERE orderId = ?";
	    try (Connection connection = Connect.getInstance().getConnection();
	         PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setInt(1, orderId);
	        ps.setInt(2, orderId);
	        int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            System.out.println("Order total was not updated for Order ID: " + orderId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

    public static Order getOrderById(int orderId) {
        String query = "SELECT * FROM Orders WHERE orderId = ?";
        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int orderUser = rs.getInt("orderUser");
                    String orderStatus = rs.getString("orderStatus");
                    String orderDate = rs.getString("orderDate");
                    double orderTotal = rs.getDouble("orderTotal");
                    return new Order(orderId, orderUser, orderStatus, orderDate, orderTotal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

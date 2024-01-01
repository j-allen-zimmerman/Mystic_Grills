package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import database.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    private static ObservableList<User> usersData = FXCollections.observableArrayList();

    public static ObservableList<User> getAllUsers() {
        usersData.clear();
        try (Connection connection = Connect.getInstance().getConnection()) {
            String query = "SELECT * FROM users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
            	while (resultSet.next()) {
            	    int id = resultSet.getInt("id");
            	    String username = resultSet.getString("username");
            	    String email = resultSet.getString("email");
            	    String role = resultSet.getString("role");
            	    usersData.add(new User(id, username, email, null, role));
            	}
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users", e);
        }
        return usersData;
    }

    public static void removeUser(int userId) {
        try (Connection connection = Connect.getInstance().getConnection()) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user", e);
        }
    }

    public static void changeUserRole(int userId, String newRole) {
        try (Connection connection = Connect.getInstance().getConnection()) {
            String query = "UPDATE users SET role = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newRole);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error changing user role", e);
        }
    }

    public static boolean isValidRole(String role) {
        List<String> validRoles = new ArrayList<>();
        validRoles.add("Admin");
        validRoles.add("Chef");
        validRoles.add("Waiter");
        validRoles.add("Cashier");
        validRoles.add("Customer");
        return validRoles.contains(role);
    }
}

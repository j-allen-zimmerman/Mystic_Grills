package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

import database.Connect;

public class LoginController {
    
	public static void loginUser(String email, String password, BiConsumer<String, String> resultHandler, BiConsumer<String, Integer> userIdHandler) {
        if (email.isEmpty() || password.isEmpty()) {
            resultHandler.accept("Error", "Email and password must be filled.");
            return;
        }

        if (!isEmailExists(email)) {
            resultHandler.accept("Error", "Email does not exist.");
            return;
        }

        String role = getUserRole(email, password);
        int userId = getUserId(email); // Fetch the user's ID

        if (role != null) {
            resultHandler.accept(role, "Login successful!");
            userIdHandler.accept(role, userId); // Pass the user's ID to the handler
        } else {
            resultHandler.accept("Error", "Incorrect password.");
        }
    }
	
	private static int getUserId(String email) {
        try (Connection connection = Connect.getInstance().getConnection()) {
            String query = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user ID", e);
        }
        return -1; // Return -1 or a sentinel value to indicate no ID was found
    }

	private static String getUserRole(String email, String password) {
	    try (Connection connection = Connect.getInstance().getConnection()) {
	        String query = "SELECT role FROM users WHERE email = ? AND password = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, email);
	            preparedStatement.setString(2, password);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    return resultSet.getString("role");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Error fetching user role", e);
	    }
	    return null;
	}


    private static boolean isEmailExists(String email) {
        try {
            Connection connection = Connect.getInstance().getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence", e);
        }
        return false;
    }

    @SuppressWarnings("unused")
	private static boolean isPasswordMatch(String email, String password) {
        try {
            Connection connection = Connect.getInstance().getConnection();
            String query = "SELECT password FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("password");
                        return storedPassword.equals(password);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking password match", e);
        }
        return false;
    }
}

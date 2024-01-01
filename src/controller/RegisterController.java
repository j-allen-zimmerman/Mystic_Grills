package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

import database.Connect;
import model.User;

public class RegisterController {
	
	public static void registerUser(User user, String confirmPassword, BiConsumer<String, String> showAlert) {
        if (user.getUsername().isEmpty()) {
            showAlert.accept("Error", "Username cannot be empty.");
            return;
        }

        if (user.getEmail().isEmpty()) {
            showAlert.accept("Error", "Email cannot be empty.");
            return;
        } else if (!isValidEmail(user.getEmail())) {
            showAlert.accept("Error", "Invalid email format, Use @gmail.com or any other mail");
            return;
        }

        if (user.getPassword().isEmpty() || confirmPassword.isEmpty()) {
            showAlert.accept("Error", "Password and confirm password cannot be empty.");
            return;
        }
        
        if (!isEmailUnique(user.getEmail())) {
            showAlert.accept("Error", "Email already exists. Please use a different email.");
            return;
        }

        if (user.getPassword().length() < 6) {
            showAlert.accept("Error", "Password must be at least 6 characters long.");
            return;
        }

        if (!user.getPassword().equals(confirmPassword)) {
            showAlert.accept("Error", "Password and confirm password do not match.");
            return;
        }

        saveUserToDatabase(user, showAlert);
    }
    
    private static boolean isEmailUnique(String email) {
        try {
            Connection connection = Connect.getInstance().getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count == 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email uniqueness", e);
        }
        return false;
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private static void saveUserToDatabase(User user, BiConsumer<String, String> showAlert) {
        try {
            Connection connection = Connect.getInstance().getConnection();
            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            showAlert.accept("Error", "Error saving user to the database");
            throw new RuntimeException("Error saving user to the database", e);
        }
        showAlert.accept("Success", "User registered successfully!");
    }
}

package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import model.User;
import controller.RegisterController;

public class RegistrationForm extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; 
        primaryStage.setTitle("Mystic Grills");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 50, 20));
        vbox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Mystic Grills");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Username:");
        Label emailLabel = new Label("Email:");
        Label passwordLabel = new Label("Password:");
        Label confirmPasswordLabel = new Label("Confirm Password:");

        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
            } else {
                User user = new User(0, usernameField.getText(), emailField.getText(), passwordField.getText());
                String confirmPassword = confirmPasswordField.getText();
                RegisterController.registerUser(user, confirmPassword, this::showAlert);
				usernameField.clear();
				emailField.clear();
				passwordField.clear();
				confirmPasswordField.clear();
            }
        });

        Label login = new Label("Already Have an Account?");
        Hyperlink loginLink = new Hyperlink("Login Now");
        loginLink.setAlignment(Pos.CENTER);
        loginLink.setOnAction(e -> {
            openLoginPage();
        });
        vbox.getChildren().addAll(titleLabel, usernameLabel, usernameField, emailLabel, emailField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, registerButton, login, loginLink);

        Scene scene = new Scene(vbox, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.start(new Stage());
        primaryStage.close();
    }
}

package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import controller.LoginController;

public class LoginPage extends Application {
    private Stage primaryStage;
    private static String username;
    private TextField emailField;
    private PasswordField passwordField;
    public static int loggedInUserId;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Mystic Grills");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 50, 20));
        vbox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Mystic Grills");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Labels
        Label emailLabel = new Label("Email:");
        Label passwordLabel = new Label("Password:");

        // Text fields
        emailField = new TextField(); 
        passwordField = new PasswordField();

        // Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
            } else {
                login();
            }
        });

        Label register = new Label("Have No Account?");
        Hyperlink registerLink = new Hyperlink("Register Now");
        registerLink.setAlignment(Pos.CENTER);
        registerLink.setOnAction(e -> {
            openRegistrationPage();
        });

        vbox.getChildren().addAll(titleLabel, emailLabel, emailField, passwordLabel, passwordField, loginButton, register, registerLink);

        Scene scene = new Scene(vbox, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    
    private void login() {
        LoginController.loginUser(
            emailField.getText(),
            passwordField.getText(),
            this::handleLoginResult,
            this::handleUserId
        );
    }
    
    private void handleUserId(String role, int userId) {
        loggedInUserId = userId; 
        if ("customer".equalsIgnoreCase(role)) {
            openCustomerMenuPage();
            primaryStage.close();
        } else if ("admin".equalsIgnoreCase(role)) {
            openAdminMenuPage();
            primaryStage.close();
        } else if("chef".equalsIgnoreCase(role)) {
        	openChefMenuPage();
        	primaryStage.close();
        } else if("waiter".equalsIgnoreCase(role)) {
        	openWaiterMenuPage();
        	primaryStage.close();
        } else if("cashier".equalsIgnoreCase(role)) {
        	openCashierMenuPage();
        	primaryStage.close();
        }
    }
    
    private void openCashierMenuPage() {
		CashierMenu cashierMenu = new CashierMenu();
		cashierMenu.start(new Stage());
		primaryStage.close();
	}

	private void openWaiterMenuPage() {
		WaiterMenu waiterMenu = new WaiterMenu();
		waiterMenu.start(new Stage());
		primaryStage.close();
		
	}

	private void openChefMenuPage() {
		ChefMenu chefMenu = new ChefMenu();
		chefMenu.start(new Stage());
		primaryStage.close();
	}

	public static int getLoggedInUserId() {
        return loggedInUserId; 
    }
    
    private void handleLoginResult(String role, String message) {
        showAlert("Login Result", message);
        if ("admin".equalsIgnoreCase(role) || "customer".equalsIgnoreCase(role)) {
            setUsername(emailField.getText());
        }
    }

	private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openRegistrationPage() {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.start(new Stage());
        primaryStage.close();
    }

    private void openAdminMenuPage() {
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.start(new Stage());
    }
    
    private void openCustomerMenuPage() {
    	CustomerMenu customerMenu = new CustomerMenu();
    	customerMenu.start(new Stage());
    	primaryStage.close();
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        LoginPage.username = username;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

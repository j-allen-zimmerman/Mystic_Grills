package view;

import controller.UserController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

import java.util.List;

public class UserManagement extends Application {

    private TableView<User> userTableView;
    private ObservableList<User> userData;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mystic Grills");

        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Buttons
        Button showAllButton = new Button("Show All Users");

        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Admin", "Chef", "Waiter", "Cashier", "Customer");
        roleDropdown.setPromptText("Select Role");

        Button updateRoleButton = new Button("Update Role");

        showAllButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateRoleButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");

        // Button actions
        showAllButton.setOnAction(e -> showAllUsers());
        updateRoleButton.setOnAction(e -> updateSelectedUserRole(roleDropdown.getValue()));

        userTableView = new TableView<>();
        userData = FXCollections.observableArrayList();
        initializeTableView();

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(showAllButton, roleDropdown, updateRoleButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainVBox.getChildren().addAll(titleLabel, buttonBox, userTableView);
        showAllUsers();
        Scene scene = new Scene(mainVBox, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
    private void initializeTableView() {
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTableView.getColumns().addAll(idColumn, usernameColumn, emailColumn, roleColumn);
        userTableView.setItems(userData);
        showAllUsers();
    }

    private void showAllUsers() {
        List<User> allUsers = UserController.getAllUsers();
        userData.clear();
        userData.addAll(allUsers);
    }

    private void updateSelectedUserRole(String newRole) {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null && newRole != null) {
            UserController.changeUserRole(selectedUser.getId(), newRole);
            selectedUser.setRole(newRole);
            showAllUsers();
            showAlert("Update Role", "Role Updated To:" + newRole);
        } else {
            showAlert("Update Role", "Please select a user and choose a role.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

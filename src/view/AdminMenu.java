package view;

import controller.UserController;
import controller.MenuItemController;
import javafx.application.Application;
import javafx.application.Platform;
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
import model.MenuItem;

import java.util.List;

public class AdminMenu extends Application {
	
	private Stage primaryStage;

    private TableView<User> userTableView;
    private ObservableList<User> userData;

    private TableView<MenuItem> menuTableView;
    private ObservableList<MenuItem> menuData;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Mystic Grills");

        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        String userName = LoginPage.getUsername();
        Label welcomeLabel = new Label("Welcome, User ID: " + userName + "!");

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Account");
        javafx.scene.control.MenuItem logoutMenuItem = new javafx.scene.control.MenuItem("Logout");
        logoutMenuItem.setOnAction(e -> handleLogout());
        fileMenu.getItems().add(logoutMenuItem);
        menuBar.getMenus().add(fileMenu);
        Label titleLabel = new Label("Admin Access Management");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        createUserTableView();
        createMenuTableView();

        Label userManagement = new Label("User Management");
        Label menuManagement = new Label("Menu Management");
        
        Button userManagementButton = new Button("User Management");
        userManagementButton.setOnAction(e -> openUserManagement());

        Button menuManagementButton = new Button("Menu Management");
        menuManagementButton.setOnAction(e -> openMenuManagement());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(userManagementButton, menuManagementButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainVBox.getChildren().addAll(menuBar, welcomeLabel, titleLabel, userManagement, userTableView, menuManagement, menuTableView, buttonBox);
        Scene scene = new Scene(mainVBox, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        menuTableView.refresh();
        userTableView.refresh();
        runDataRefreshThread();
    }


    @SuppressWarnings("unchecked")
	private void createUserTableView() {
        userTableView = new TableView<>();
        userData = FXCollections.observableArrayList();

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

    @SuppressWarnings("unchecked")
	private void createMenuTableView() {
        menuTableView = new TableView<>();
        menuData = FXCollections.observableArrayList();

        TableColumn<MenuItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemID"));

        TableColumn<MenuItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));

        TableColumn<MenuItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemDescription"));

        TableColumn<MenuItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemPrice"));

        menuTableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn);
        menuTableView.setItems(menuData);
        showAllMenus();
    }


    private void showAllMenus() {
        List<MenuItem> allMenus = MenuItemController.getAllMenuItems();
        menuData.clear();
        menuData.addAll(allMenus);
    }

    private void openUserManagement() {
        UserManagement userManagement = new UserManagement();
        Stage userManagementStage = new Stage();
        userManagement.start(userManagementStage);
    }

    private void openMenuManagement() {
        MenuManagement menuManagement = new MenuManagement();
        Stage menuManagementStage = new Stage();
        menuManagement.start(menuManagementStage);
    }

    private void runDataRefreshThread() {
        Thread dataRefreshThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    refreshUserData();
                    refreshMenuData();
                });
            }
        });

        dataRefreshThread.setDaemon(true);
        dataRefreshThread.start();
    }

    private void refreshUserData() {
        List<User> users = UserController.getAllUsers();
        userData.clear();
        userData.addAll(users);
    }

    private void refreshMenuData() {
        List<MenuItem> menuItems = MenuItemController.getAllMenuItems();
        menuData.clear();
        menuData.addAll(menuItems);
    }
    
    
    private void handleLogout() {
        showLoginScene();
    }

    private void showLoginScene() {
        LoginPage login = new LoginPage();
        Stage loginStage = new Stage();
        login.start(loginStage);
        primaryStage.close();
    }
}

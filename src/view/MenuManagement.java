package view;

import controller.MenuItemController;
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
import model.MenuItem;

import java.util.List;

public class MenuManagement extends Application {

    private TableView<MenuItem> menuTableView;
    private ObservableList<MenuItem> menuData;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mystic Grills");

        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Menu Management");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Buttons
        Button showAllButton = new Button("Show All Menus");
        Button addButton = new Button("Add Menu");
        Button deleteButton = new Button("Delete Menu");
        Button updateButton = new Button("Update Menu");

        // Set styles for buttons
        showAllButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        updateButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");

        // Button actions
        showAllButton.setOnAction(e -> showAllMenus());
        addButton.setOnAction(e -> openAddMenuForm());
        deleteButton.setOnAction(e -> openDeleteMenuForm());
        updateButton.setOnAction(e -> openUpdateMenuForm());

        // Initialize TableView
        menuTableView = new TableView<>();
        menuData = FXCollections.observableArrayList();
        initializeTableView();

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(showAllButton, addButton, deleteButton, updateButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainVBox.getChildren().addAll(titleLabel, buttonBox, menuTableView);
        showAllMenus();
        Scene scene = new Scene(mainVBox, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
	private void initializeTableView() {
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

    private void openAddMenuForm() {
    	AddMenu addMenu = new AddMenu();
        Stage addStage = new Stage();
        addMenu.start(addStage);
        addStage.setOnHidden(e -> showAllMenus());
    }

    private void openDeleteMenuForm() {
        MenuItem selectedMenuItem = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedMenuItem != null) {
            MenuItemController.deleteMenuItem(selectedMenuItem.getMenuItemID(), this::handleDeleteResult);
        } else {
            showAlert("Delete Menu", "Please select a menu item to delete.");
        }
    }

    private void handleDeleteResult(String message, String content) {
        showAlert(message, content);
        showAllMenus();
    }

    private void openUpdateMenuForm() {
    	MenuItem selectedMenuItem = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedMenuItem != null) {
            UpdateMenu updateMenuPage = new UpdateMenu(selectedMenuItem);
            Stage updateStage = new Stage();
            updateMenuPage.start(updateStage);

            // Set an event handler for when the UpdateMenu form is closed
            updateStage.setOnHidden(e -> showAllMenus());
        } else {
            showAlert("Update Menu", "Please select a menu item to update.");
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

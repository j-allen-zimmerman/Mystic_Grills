package view;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import model.OrderItem; // Assuming you have an OrderItem model
import controller.MenuItemController;
import controller.OrderController;
import controller.OrderItemController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMenu extends Application {

    private TableView<MenuItem> menuTableView;
    private TableView<OrderItem> cartTableView;
    private ObservableList<MenuItem> menuData;
    private ObservableList<OrderItem> cartData;
    private TextField quantityInput, updateQuantity;
    private Button addButton, submitOrderButton, orderHistoryButton, updateQuantityButton, deleteOrderButton;
    private Map<MenuItem, Integer> orderMap = new HashMap<>();
    private Label orderLabel, menuLabel;
    private OrderItem selectedOrderItem;
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        primaryStage.setTitle("Mystic Grills");
    	VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        
        createMenuTableView();
        createCartTableView();
        
        String userID = String.valueOf(LoginPage.getLoggedInUserId());
        String userName = LoginPage.getUsername();
        Label welcomeLabel = new Label("Welcome, User : " + userID + ", " + userName + "!");
        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Account");
        javafx.scene.control.MenuItem logoutMenuItem = new javafx.scene.control.MenuItem("Logout");
        logoutMenuItem.setOnAction(e -> handleLogout());
        fileMenu.getItems().add(logoutMenuItem);
        menuBar.getMenus().add(fileMenu);

        quantityInput = new TextField();
        quantityInput.setPromptText("Quantity");

        addButton = new Button("Add to Order");
        addButton.setOnAction(e -> addToOrder());
        
        updateQuantity = new TextField();
        updateQuantity.setPromptText("Update Quantity");

        submitOrderButton = new Button("Submit Order");
        submitOrderButton.setOnAction(e -> submitOrder());
        
        orderHistoryButton = new Button("Order History");
        orderHistoryButton.setOnAction(e -> orderHistory());
        updateQuantityButton = new Button("Update Quantity");
        updateQuantityButton.setOnAction(e -> updateOrderItemQuantity());

        deleteOrderButton = new Button("Delete Order");
        deleteOrderButton.setOnAction(e -> deleteOrderItem());

        HBox buttonBar = new HBox(10, addButton, updateQuantityButton, deleteOrderButton);
        buttonBar.setAlignment(Pos.CENTER);
        HBox addMenu = new HBox(30, menuTableView, quantityInput, addButton);
        buttonBar.setAlignment(Pos.CENTER);
        
        menuLabel = new Label("Mystic Grills Menu");
        menuLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        orderLabel = new Label("Your Carts");
        orderLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        mainVBox.getChildren().addAll(menuBar, welcomeLabel, menuLabel, addMenu, orderLabel, cartTableView, updateQuantity, buttonBar, submitOrderButton, orderHistoryButton);

        Scene scene = new Scene(mainVBox, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void orderHistory() {
		OrderHistory orderHistory = new OrderHistory();
		Stage orderHistoryStage = new Stage();
		orderHistory.start(orderHistoryStage);
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

    @SuppressWarnings("unchecked")
	private void createCartTableView() {
        cartTableView = new TableView<>();
        cartData = FXCollections.observableArrayList();
        TableColumn<OrderItem, Integer> menuItemIdColumn = new TableColumn<>("Menu Item ID");
        menuItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemID"));

        TableColumn<OrderItem, String> menuItemNameColumn = new TableColumn<>("Name");
        menuItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));

        TableColumn<OrderItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity() * cellData.getValue().getMenuItemPrice()));

        cartTableView.getColumns().addAll(menuItemIdColumn, menuItemNameColumn, quantityColumn, totalPriceColumn);
        cartTableView.setItems(cartData);
    }


    private void showAllMenus() {
        List<MenuItem> allMenus = MenuItemController.getAllMenuItems();
        menuData.clear();
        menuData.addAll(allMenus);
    }

    private void addToOrder() {
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityInput.getText());
            if (selectedItem != null && quantity >= 1) {
                orderMap.put(selectedItem, orderMap.getOrDefault(selectedItem, 0) + quantity);
                updateCartData();
                quantityInput.clear();
            } else {
            
            }
        } catch (NumberFormatException e) {
        	showAlert("Add To Order", "Please Select The Item More Than 0");
        }
    }
    
    private void submitOrder() {
        if (cartData.isEmpty()) {
            showAlert("Submit Order", "No items in the cart to submit.");
            return;
        }

        int userId = LoginPage.getLoggedInUserId();
        double total = updateTotalAmountAndRefresh();
        int orderId = OrderController.createOrder(userId, "Pending", total);

        if (orderId != -1) {
            for (OrderItem item : cartData) {
                OrderItemController.createOrderItem(orderId, item.getMenuItemID(), item.getQuantity());
            }
            cartData.clear();
            orderMap.clear();
            showAlert("Submit Order", "Order submitted successfully.");
        } else {
            showAlert("Submit Order", "Failed to submit the order.");
        }
    }
    
    private int currentOrderId = -1; // Assuming -1 indicates no current order

    private double updateTotalAmountAndRefresh() {
        @SuppressWarnings("unused")
		double total;
        if (currentOrderId != -1) {
            return total = OrderController.getOrderTotal(currentOrderId);
        } else {
            return total = calculateTotalOrderAmount();
        }
    }

    private double calculateTotalOrderAmount() {
        double total = 0;
        for (OrderItem item : cartData) {
            total += item.getQuantity() * item.getMenuItemPrice();
        }
        return total;
    }

    private void updateCartData() {
        cartData.clear();
        orderMap.forEach((menuItem, quantity) -> {
            OrderItem orderItem = new OrderItem(menuItem, quantity);
            cartData.add(orderItem);
        });
    }
    
    private void updateOrderItemQuantity() {
        selectedOrderItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedOrderItem != null) {
            try {
                int newQuantity = Integer.parseInt(updateQuantity.getText());
                if (newQuantity > 0) {
                    orderMap.put(selectedOrderItem.getMenuItem(), newQuantity);
                    updateCartData();
                    updateQuantity.clear();
                } else {
                    showAlert("Update Quantity", "Quantity must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                showAlert("Update Quantity", "Invalid quantity format.");
            }
        } else {
            showAlert("Update Quantity", "No item selected.");
        }
    }


    private void deleteOrderItem() {
        selectedOrderItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedOrderItem != null) {
            orderMap.remove(selectedOrderItem.getMenuItem());
            updateCartData();
        } else {
            showAlert("Delete Order", "No item selected.");
        }
    }


    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

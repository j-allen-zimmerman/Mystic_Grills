package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.Order;
import model.OrderItem;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import controller.OrderController;
import controller.OrderItemController;

public class ChefMenu extends Application {

	private TableView<Order> orderTable;
    private ObservableList<Order> orderData = FXCollections.observableArrayList();
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        primaryStage.setTitle("Mystic Grills");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Account");
        javafx.scene.control.MenuItem logoutMenuItem = new javafx.scene.control.MenuItem("Logout");
        logoutMenuItem.setOnAction(e -> handleLogout());
        fileMenu.getItems().add(logoutMenuItem);
        menuBar.getMenus().add(fileMenu);

        Label titleLabel = new Label("Chef Menu");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        Label desc = new Label("Double click to see each details order");
        desc.setStyle("-fx-font-weight: bold;");
        
        orderTable = new TableView<>();
        setupOrderTable();

        Button prepareButton = new Button("Prepare Order");
        prepareButton.setOnAction(e -> prepareOrder(orderTable.getSelectionModel().getSelectedItem()));

        layout.getChildren().addAll(menuBar, titleLabel, orderTable, prepareButton, desc);
        Scene scene = new Scene(layout, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadPendingOrders();
    }

    @SuppressWarnings("unchecked")
	private void setupOrderTable() {
        TableColumn<Order, Integer> idColumn = new TableColumn<>("Order ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        orderTable.getColumns().addAll(idColumn, statusColumn);
        orderTable.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Order selectedOrder = row.getItem();
                    showOrderDetails(selectedOrder);
                }
            });
            return row;
        });
        orderTable.setItems(orderData);
    }
    
    private void showOrderDetails(Order order) {
        Stage detailsStage = new Stage();
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Order Details for Order ID: " + order.getOrderId());
        headerLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TableView<OrderItem> detailsTable = new TableView<>();
        setupDetailsTable(detailsTable, order.getOrderId());
        
        TextField updateQuantity = new TextField();
        updateQuantity.setPromptText("Enter New Quantity");
        Button updateButton = new Button("Update Quantity");
        Button removeButton = new Button("Remove Item");


        updateButton.setOnAction(e -> {
            OrderItem selectedItem = detailsTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    int newQuantity = Integer.parseInt(updateQuantity.getText());
                    if (newQuantity > 0) {
                        OrderItemController.updateOrderItemQuantity(selectedItem, newQuantity);
                        updateTotalAmountAndRefresh(order);
                        loadOrderItems(detailsTable, order.getOrderId());
                    } else {
                        showAlert("Invalid Quantity", "Quantity must be greater than zero.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter a valid quantity.");
                }
            }
        });

        removeButton.setOnAction(e -> {
            OrderItem selectedItem = detailsTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                OrderItemController.deleteOrderItem(selectedItem.getOrderID(), selectedItem.getMenuItemID());
                updateTotalAmountAndRefresh(order);
                loadOrderItems(detailsTable, order.getOrderId());
            }
        });
        
        HBox controlBox = new HBox(10, updateQuantity, updateButton, removeButton);
        controlBox.setAlignment(Pos.CENTER);

        mainVBox.getChildren().addAll(headerLabel, detailsTable, controlBox);
        Scene scene = new Scene(mainVBox, 600, 800);
        detailsStage.setScene(scene);
        detailsStage.setTitle("Order Details");
        detailsStage.show();
    }
    
    @SuppressWarnings("unchecked")
	private void setupDetailsTable(TableView<OrderItem> table, int orderId) {
        TableColumn<OrderItem, String> nameColumn = new TableColumn<>("Menu Item Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        TableColumn<OrderItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<OrderItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemPrice"));
        table.getColumns().addAll(nameColumn, quantityColumn, priceColumn);
        loadOrderItems(table, orderId);
    }

    private void loadOrderItems(TableView<OrderItem> table, int orderId) {
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList(OrderItemController.getOrderItemsByOrderId(orderId));
        table.setItems(orderItems);
    }

    private void loadPendingOrders() {
        ObservableList<Order> pendingOrders = FXCollections.observableArrayList(OrderController.getPendingOrders());
        orderTable.setItems(pendingOrders);
    }

    private void prepareOrder(Order selectedOrder) {
        if (selectedOrder != null && "Pending".equals(selectedOrder.getOrderStatus())) {
            OrderController.updateOrderStatus(selectedOrder.getOrderId(), "Prepared");
            loadPendingOrders();
        } else {
            showAlert("Error", "No pending order selected or order already prepared.");
        }
    }
    
    private void updateTotalAmountAndRefresh(Order order) {
        OrderController.updateOrderTotalInDatabase(order.getOrderId());
        refreshOrderTableView(order.getOrderId());
    }

    private void refreshOrderTableView(int orderId) {
        Order updatedOrder = OrderController.getOrderById(orderId);
        if (updatedOrder != null) {
            boolean orderFound = false;
            for (int i = 0; i < orderData.size(); i++) {
                if (orderData.get(i).getOrderId() == orderId) {
                    orderData.set(i, updatedOrder);
                    orderFound = true;
                    break;
                }
            }
            if (orderFound) {
                orderTable.refresh();
            }
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

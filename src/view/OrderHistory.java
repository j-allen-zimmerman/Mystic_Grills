package view;

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
import model.Order;
import model.OrderItem;

import java.util.List;

import controller.MenuItemController;
import controller.OrderController;
import controller.OrderItemController;

public class OrderHistory extends Application {

    private TableView<Order> orderHistoryTable;
    private ObservableList<Order> orderData;
    private TableView<MenuItem> menuTableView;
    private ObservableList<MenuItem> menuData;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mystic Grills");

        Label historyLabel = new Label("Your Recent Orders");
        historyLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        orderHistoryTable = new TableView<>();
        
        setupOrderHistoryTable();
        Label desc = new Label("Double click to see your details order");
        desc.setStyle("-fx-font-weight: bold;");

        
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        mainVBox.getChildren().addAll(historyLabel, orderHistoryTable, desc);
        Scene scene = new Scene(mainVBox, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
	private void setupOrderHistoryTable() {
        TableColumn<Order, Integer> idColumn = new TableColumn<>("Order ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        TableColumn<Order, String> dateColumn = new TableColumn<>("Order Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        TableColumn<Order, Double> totalColumn = new TableColumn<>("Total Amount");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotal"));
        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        orderHistoryTable.getColumns().addAll(idColumn, dateColumn, totalColumn, statusColumn);
        orderHistoryTable.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Order selectedOrder = row.getItem();
                    showOrderDetails(selectedOrder);
                }
            });
            return row;
        });
        loadOrderData();
    }

    private void showOrderDetails(Order order) {
        Stage detailsStage = new Stage();
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        Label menuLabel = new Label("Mystic Grills Menu");
        menuLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        Label headerLabel = new Label("Order Details for Order ID: " + order.getOrderId());
        headerLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TableView<OrderItem> detailsTable = new TableView<>();
        setupDetailsTable(detailsTable, order.getOrderId());

        menuTableView = new TableView<>();
        setupMenuTableView();
        
        
        TextField quantityInput = new TextField();
        quantityInput.setPromptText("Enter New Quantity");
        
        
        TextField updateQuantity = new TextField();
        updateQuantity.setPromptText("Enter New Quantity");
        Button updateButton = new Button("Update Quantity");
        Button removeButton = new Button("Remove Item");

        Button addItemButton = new Button("Add Item to Order");
        addItemButton.setOnAction(e -> {
            MenuItem selectedMenuItem = menuTableView.getSelectionModel().getSelectedItem();
            if (selectedMenuItem != null && !quantityInput.getText().isEmpty()) {
                try {
                    int quantityToAdd = Integer.parseInt(quantityInput.getText());
                    if (quantityToAdd > 0) {
                        OrderItemController.addNewItemToOrder(order.getOrderId(), selectedMenuItem.getMenuItemID(), quantityToAdd);
                        updateTotalAmountAndRefresh(order);
                        loadOrderItems(detailsTable, order.getOrderId());
                    } else {
                        showAlert("Invalid Quantity", "Quantity must be greater than zero.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter a valid quantity.");
                }
            } else {
                showAlert("Missing Information", "Please select a menu item and enter a quantity.");
            }
        });

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
        
        HBox menuBox = new HBox(10, quantityInput, addItemButton);
        menuBox.setAlignment(Pos.CENTER);

        HBox controlBox = new HBox(10, updateQuantity, updateButton, removeButton);
        controlBox.setAlignment(Pos.CENTER);

        mainVBox.getChildren().addAll(menuLabel, menuTableView, menuBox, headerLabel, detailsTable, controlBox);
        Scene scene = new Scene(mainVBox, 600, 800);
        detailsStage.setScene(scene);
        detailsStage.setTitle("Order Details");
        detailsStage.show();
    }

    @SuppressWarnings("unchecked")
	private void setupMenuTableView() {
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

    private void loadOrderData() {
        int userId = LoginPage.getLoggedInUserId();
        orderData = FXCollections.observableArrayList(OrderController.getOrdersByUserId(userId));
        orderHistoryTable.setItems(orderData);
    }

    private void updateTotalAmountAndRefresh(Order order) {
        OrderController.updateOrderTotalInDatabase(order.getOrderId());
        refreshOrderTableView(order.getOrderId());
    }

    private void refreshOrderTableView(int orderId) {
        Order updatedOrder = OrderController.getOrderById(orderId);
        if (updatedOrder != null) {
            for (int i = 0; i < orderData.size(); i++) {
                if (orderData.get(i).getOrderId() == orderId) {
                    orderData.set(i, updatedOrder);
                    break;
                }
            }
            orderHistoryTable.refresh();
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

package view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import controller.OrderController;
import controller.ReceiptController;
import model.Order;
import model.Receipt;

import java.sql.Date;

public class CashierMenu extends Application {

	private Stage primaryStage;
	private TableView<Order> orderTable;
	private ObservableList<Order> orderData = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
    	primaryStage.setTitle("Mystic Grills");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 50, 20));
        layout.setAlignment(Pos.CENTER);

        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Account");
        javafx.scene.control.MenuItem logoutMenuItem = new javafx.scene.control.MenuItem("Logout");
        logoutMenuItem.setOnAction(e -> handleLogout());
        fileMenu.getItems().add(logoutMenuItem);
        menuBar.getMenus().add(fileMenu);
        
        Label titleLabel = new Label("Cashier Menu");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        orderTable = new TableView<>();
        setupOrderTable();

        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Order ID");
        
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        	if (newSelection != null) {
        		orderIdField.setText(String.valueOf(newSelection.getOrderId()));
        	}
        });
        ComboBox<String> paymentTypeBox = new ComboBox<>();
        paymentTypeBox.getItems().addAll("Cash", "Debit", "Credit");

        TextField amountField = new TextField();
        amountField.setPromptText("Payment Amount");

        Button processButton = new Button("Process Payment");
        processButton.setOnAction(e -> processPayment(orderIdField.getText(), paymentTypeBox.getValue(), amountField.getText()));
        
        Button receiptPage = new Button("See All Receipt");
        receiptPage.setOnAction(e -> openReceiptPage());
        
        layout.getChildren().addAll(menuBar, titleLabel, orderTable, orderIdField, paymentTypeBox, amountField, processButton, receiptPage);
        Scene scene = new Scene(layout, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cashier Menu");
        primaryStage.show();
        
        loadServedOrders();
    }
    
    @SuppressWarnings("unchecked")
	private void setupOrderTable() {
        TableColumn<Order, Integer> idColumn = new TableColumn<>("Order ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        

        TableColumn<Order, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("orderUser"));

        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        
        TableColumn<Order, String> dateColumn = new TableColumn<>("Status");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        
        TableColumn<Order, String> totalColumn = new TableColumn<>("Total Amount");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("orderTotal"));

        orderTable.getColumns().addAll(idColumn, userColumn, statusColumn, dateColumn, totalColumn);
        orderTable.setItems(orderData);
    }
    
    private void loadServedOrders() {
        ObservableList<Order> preparedOrders = FXCollections.observableArrayList(OrderController.getServedOrders());
        orderTable.setItems(preparedOrders);
    }

    private void processPayment(String orderIdStr, String paymentType, String amountStr) {
        if (paymentType == null || paymentType.isEmpty()) {
            showAlert("Error", "Payment type must be selected.");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            double amount = Double.parseDouble(amountStr);

            // Retrieve the order to check the total amount
            Order selectedOrder = OrderController.getOrderById(orderId);
            if (selectedOrder == null) {
                showAlert("Error", "Invalid Order ID.");
                return;
            }

            if (amount < selectedOrder.getOrderTotal()) {
                showAlert("Error", "Payment amount is less than the order's total bill.");
                return;
            }

            Receipt receipt = new Receipt(0, orderId, amount, new Date(System.currentTimeMillis()), paymentType);
            boolean success = ReceiptController.processPayment(receipt);
            if (success) {
                showAlert("Success", "Payment processed successfully.");
                // Optionally, update the order status to 'Paid'
                OrderController.updateOrderStatus(orderId, "Paid");
                loadServedOrders();
            } else {
                showAlert("Error", "Payment processing failed.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input.");
        }
    }
    
    private void openReceiptPage() {
        ReceiptPage receiptPage = new ReceiptPage();
        Stage receiptPageStage = new Stage();
        receiptPage.start(receiptPageStage);
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

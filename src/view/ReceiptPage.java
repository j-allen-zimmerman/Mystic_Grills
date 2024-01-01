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
import controller.OrderItemController;
import controller.ReceiptController;
import model.OrderItem;
import model.Receipt;

import java.sql.Date;

public class ReceiptPage extends Application {

    private TableView<Receipt> receiptTable;
    private ObservableList<Receipt> receiptData = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Receipts");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 50, 20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("All Receipts");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        receiptTable = new TableView<>();
        setupReceiptTable();

        layout.getChildren().addAll(titleLabel, receiptTable);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadReceipts();
    }

    @SuppressWarnings("unchecked")
    private void setupReceiptTable() {
        TableColumn<Receipt, Integer> receiptIdColumn = new TableColumn<>("Receipt ID");
        receiptIdColumn.setCellValueFactory(new PropertyValueFactory<>("receiptID"));

        TableColumn<Receipt, Integer> orderIdColumn = new TableColumn<>("Order ID");
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("receiptOrder"));

        TableColumn<Receipt, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("receiptAmount"));

        TableColumn<Receipt, Date> dateColumn = new TableColumn<>("Payment Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Receipt, String> typeColumn = new TableColumn<>("Payment Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));

        receiptTable.getColumns().addAll(receiptIdColumn, orderIdColumn, amountColumn, dateColumn, typeColumn);
        receiptTable.setRowFactory(tv -> {
            TableRow<Receipt> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Receipt selectedOrder = row.getItem();
                    showReceiptDetails(selectedOrder);
                }
            });
            return row;
        });
        receiptTable.setItems(receiptData);
    }

    private void showReceiptDetails(Receipt selectedOrder) {
    	Stage detailsStage = new Stage();
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(20, 20, 50, 20));
        mainVBox.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("Order Details for Receipt ID: " + selectedOrder.getReceiptOrder());
        headerLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TableView<OrderItem> detailsTable = new TableView<>();
        setupDetailsTable(detailsTable, selectedOrder.getReceiptOrder());
        
        mainVBox.getChildren().addAll(headerLabel, detailsTable);
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

	private void loadReceipts() {
        ObservableList<Receipt> allReceipts = FXCollections.observableArrayList(ReceiptController.getAllReceipts());
        receiptTable.setItems(allReceipts);
    }
}

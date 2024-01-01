// AddMenu class
package view;

import controller.MenuItemController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MenuItem;

public class AddMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mystic Grills");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 50, 20));
        vbox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Add Menu Item");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Labels
        Label nameLabel = new Label("Menu Item Name:");
        Label descriptionLabel = new Label("Menu Item Description:");
        Label priceLabel = new Label("Menu Item Price:");

        // Text fields
        TextField nameField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();

        // Button
        Button addButton = new Button("Add Menu Item");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            String itemName = nameField.getText();
            String itemDescription = descriptionField.getText();
            String priceText = priceField.getText();

            if (itemName.isEmpty() || itemDescription.isEmpty() || priceText.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            try {
                double itemPrice = Double.parseDouble(priceText);
                MenuItem menuItem = new MenuItem(itemName, itemDescription, itemPrice);

                // Call the controller to add the menu item
                boolean success = MenuItemController.addMenuItem(menuItem, this::showAlert);

                if (success) {
                    primaryStage.close();
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid price format. Please enter a valid number.");
            }
        });
        vbox.getChildren().addAll(titleLabel, nameLabel, nameField, descriptionLabel, descriptionField, priceLabel, priceField, addButton);

        Scene scene = new Scene(vbox, 300, 500);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

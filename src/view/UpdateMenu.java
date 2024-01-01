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

public class UpdateMenu extends Application {

    private MenuItem menuItem;
    private TextField nameField;
    private TextArea descriptionArea;
    private TextField priceField;

    public UpdateMenu(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mystic Grills");

        VBox updateMenuBox = new VBox(10);
        updateMenuBox.setPadding(new Insets(20, 20, 20, 20));
        updateMenuBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Update Menu Item");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(menuItem.getMenuItemName());

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setText(menuItem.getMenuItemDescription());

        priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setText(String.valueOf(menuItem.getMenuItemPrice()));

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        updateButton.setOnAction(e -> updateMenuItem());

        updateMenuBox.getChildren().addAll(titleLabel, nameField, descriptionArea, priceField, updateButton);

        Scene scene = new Scene(updateMenuBox, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateMenuItem() {
        // Retrieve the updated values from the form
        String updatedName = nameField.getText();
        String updatedDescription = descriptionArea.getText();
        double updatedPrice = Double.parseDouble(priceField.getText());

        // Create a new MenuItem with updated values
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setMenuItemID(menuItem.getMenuItemID());
        updatedMenuItem.setMenuItemName(updatedName);
        updatedMenuItem.setMenuItemDescription(updatedDescription);
        updatedMenuItem.setMenuItemPrice(updatedPrice);

        // Call the controller to update the menu item
        MenuItemController.updateMenuItem(updatedMenuItem, this::showAlert);
    }

    private void showAlert(String message, String content) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}

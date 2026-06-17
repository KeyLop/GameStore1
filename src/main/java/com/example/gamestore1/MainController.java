package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        if (LoginController.currentUser != null) {
            welcomeLabel.setText("Добро пожаловать, " + LoginController.currentUser.name + "!");
        }
    }

    public void showCatalog() { load("catalog.fxml"); }
    public void showLibrary() { load("library.fxml"); }
    public void showCart()    { load("cart.fxml"); }
    public void showProfile() { load("profile.fxml"); }
    public void showFriends() { load("friends.fxml"); }
    public void showAdmin()   { load("admin.fxml"); }

    private void load(String fxml) {
        try {
            contentArea.getChildren().clear();
            // ПРАВИЛЬНЫЙ ПУТЬ
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gamestore1/" + fxml)
            );
            contentArea.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
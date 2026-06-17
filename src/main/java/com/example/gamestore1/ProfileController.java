package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label dateLabel;
    @FXML private TextField editNameField;
    @FXML private TextField editEmailField;
    @FXML private PasswordField editPassField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        User user = LoginController.currentUser;
        if (user != null) {
            nameLabel.setText(user.name);
            emailLabel.setText(user.email);
            editNameField.setText(user.name);
            editEmailField.setText(user.email);
        }
    }

    @FXML
    public void saveProfile() {
        String name = editNameField.getText();
        String email = editEmailField.getText();
        String pass = editPassField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Заполните все поля!");
            return;
        }

        try {
            Connection conn = Database.get();
            String query = "UPDATE [Пользователь] SET Имя = ?, Email = ?";
            if (!pass.isEmpty()) {
                query += ", Пароль = ?";
            }
            query += " WHERE ID_Пользователя = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            if (!pass.isEmpty()) {
                stmt.setString(3, pass);
                stmt.setInt(4, LoginController.currentUser.id);
            } else {
                stmt.setInt(3, LoginController.currentUser.id);
            }
            stmt.executeUpdate();

            LoginController.currentUser.name = name;
            LoginController.currentUser.email = email;
            nameLabel.setText(name);
            emailLabel.setText(email);
            messageLabel.setText("✅ Профиль обновлен!");

        } catch (Exception e) {
            messageLabel.setText("❌ Ошибка: " + e.getMessage());
        }
    }
}
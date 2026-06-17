package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    // === ВХОД ===
    @FXML private TextField loginField;
    @FXML private PasswordField passField;

    // === РЕГИСТРАЦИЯ ===
    @FXML private TextField regNameField;
    @FXML private TextField regEmailField;
    @FXML private TextField regLoginField;
    @FXML private PasswordField regPassField;
    @FXML private PasswordField regConfirmField;

    @FXML private Label errorLabel;

    public static User currentUser = null;

    @FXML
    public void handleLogin() {
        String login = loginField.getText();
        String pass = passField.getText();

        if (login.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Заполните все поля!");
            errorLabel.setVisible(true);
            return;
        }

        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM [Пользователь] WHERE Логин = ? AND Пароль = ?"
            );
            stmt.setString(1, login);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUser = new User();
                currentUser.id = rs.getInt("ID_Пользователя");
                currentUser.name = rs.getString("Имя");
                currentUser.login = rs.getString("Логин");
                currentUser.email = rs.getString("Email");

                openMainWindow();
            } else {
                errorLabel.setText("Неверный логин или пароль!");
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void handleRegister() {
        String name = regNameField.getText();
        String email = regEmailField.getText();
        String login = regLoginField.getText();
        String pass = regPassField.getText();
        String confirm = regConfirmField.getText();

        if (name.isEmpty() || email.isEmpty() || login.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("Заполните все поля!");
            errorLabel.setVisible(true);
            return;
        }

        if (!email.contains("@")) {
            errorLabel.setText("Введите корректный email!");
            errorLabel.setVisible(true);
            return;
        }

        if (pass.length() < 4) {
            errorLabel.setText("Пароль должен быть минимум 4 символа!");
            errorLabel.setVisible(true);
            return;
        }

        if (!pass.equals(confirm)) {
            errorLabel.setText("Пароли не совпадают!");
            errorLabel.setVisible(true);
            return;
        }

        try {
            Connection conn = Database.get();

            // Проверка на существование логина
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM [Пользователь] WHERE Логин = ?"
            );
            checkStmt.setString(1, login);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                errorLabel.setText("Логин уже занят!");
                errorLabel.setVisible(true);
                return;
            }
            checkStmt.close();

            // Проверка на существование email
            checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM [Пользователь] WHERE Email = ?"
            );
            checkStmt.setString(1, email);
            rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                errorLabel.setText("Email уже используется!");
                errorLabel.setVisible(true);
                return;
            }
            checkStmt.close();

            // ✅ ВСТАВКА БЕЗ ID — БАЗА САМА ЕГО ДАСТ
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO [Пользователь] (Имя, Email, Логин, Пароль, [Дата регистрации]) " +
                            "VALUES (?, ?, ?, ?, GETDATE())"
            );
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, login);
            stmt.setString(4, pass);
            stmt.executeUpdate();
            stmt.close();

            errorLabel.setText("✅ Регистрация успешна! Теперь войдите.");
            errorLabel.setStyle("-fx-text-fill: #6fcf97;");
            errorLabel.setVisible(true);

            regNameField.clear();
            regEmailField.clear();
            regLoginField.clear();
            regPassField.clear();
            regConfirmField.clear();

        } catch (Exception e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    private void openMainWindow() {
        try {
            Stage stage = (Stage) loginField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gamestore1/main.fxml")
            );
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("GameStore");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
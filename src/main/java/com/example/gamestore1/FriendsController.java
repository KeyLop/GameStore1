package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FriendsController {

    @FXML private TextField searchField;
    @FXML private ListView<String> friendsListView;
    @FXML private ListView<String> requestsListView;
    @FXML private ListView<String> sentListView;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadFriends();
        loadRequests();
        loadSentRequests();
    }

    private void loadFriends() {
        if (LoginController.currentUser == null) return;
        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.Имя FROM [Пользователь] u " +
                            "JOIN [Друзья] f ON u.ID_Пользователя = f.ID_Друга " +
                            "WHERE f.ID_Пользователя = ? AND f.Статус = 'ACCEPTED'"
            );
            stmt.setInt(1, LoginController.currentUser.id);
            ResultSet rs = stmt.executeQuery();
            friendsListView.getItems().clear();
            while (rs.next()) {
                friendsListView.getItems().add(rs.getString("Имя"));
            }
        } catch (Exception e) {
            messageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void loadRequests() {
        if (LoginController.currentUser == null) return;
        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.Имя FROM [Пользователь] u " +
                            "JOIN [Друзья] f ON u.ID_Пользователя = f.ID_Пользователя " +
                            "WHERE f.ID_Друга = ? AND f.Статус = 'PENDING'"
            );
            stmt.setInt(1, LoginController.currentUser.id);
            ResultSet rs = stmt.executeQuery();
            requestsListView.getItems().clear();
            while (rs.next()) {
                requestsListView.getItems().add(rs.getString("Имя") + " (хочет добавить в друзья)");
            }
        } catch (Exception e) {
            messageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void loadSentRequests() {
        if (LoginController.currentUser == null) return;
        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.Имя FROM [Пользователь] u " +
                            "JOIN [Друзья] f ON u.ID_Пользователя = f.ID_Друга " +
                            "WHERE f.ID_Пользователя = ? AND f.Статус = 'PENDING'"
            );
            stmt.setInt(1, LoginController.currentUser.id);
            ResultSet rs = stmt.executeQuery();
            sentListView.getItems().clear();
            while (rs.next()) {
                sentListView.getItems().add("📤 Заявка отправлена: " + rs.getString("Имя"));
            }
        } catch (Exception e) {
            messageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    // ============================================================
    //  ПОИСК (ИСПРАВЛЕННЫЙ)
    // ============================================================
    @FXML
    public void searchUsers() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            messageLabel.setText("⚠️ Введите имя или логин для поиска");
            return;
        }

        try {
            Connection conn = Database.get();

            // Ищем по имени ИЛИ по логину
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT ID_Пользователя, Имя, Логин FROM [Пользователь] " +
                            "WHERE (Имя LIKE ? OR Логин LIKE ?) AND ID_Пользователя != ?"
            );
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setInt(3, LoginController.currentUser.id);

            ResultSet rs = stmt.executeQuery();

            List<String> results = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("Имя");
                String login = rs.getString("Логин");
                results.add(name + " (@ " + login + ")");
            }

            if (results.isEmpty()) {
                messageLabel.setText("❌ Пользователи не найдены");
                return;
            }

            // Показываем диалог с результатами
            StringBuilder sb = new StringBuilder("🔍 Найдены пользователи:\n\n");
            for (String user : results) {
                sb.append("• ").append(user).append("\n");
            }

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Результаты поиска");
            alert.setHeaderText(null);
            alert.setContentText(sb.toString());
            ButtonType sendBtn = new ButtonType("Отправить заявку всем");
            ButtonType cancelBtn = new ButtonType("Отмена");
            alert.getButtonTypes().setAll(sendBtn, cancelBtn);

            alert.showAndWait().ifPresent(btn -> {
                if (btn == sendBtn) {
                    try {
                        // Отправляем заявку всем найденным
                        PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO [Друзья] (ID_Пользователя, ID_Друга, Статус, ДатаЗапроса) " +
                                        "SELECT ?, ID_Пользователя, 'PENDING', GETDATE() FROM [Пользователь] " +
                                        "WHERE (Имя LIKE ? OR Логин LIKE ?) AND ID_Пользователя != ?"
                        );
                        insertStmt.setInt(1, LoginController.currentUser.id);
                        insertStmt.setString(2, searchPattern);
                        insertStmt.setString(3, searchPattern);
                        insertStmt.setInt(4, LoginController.currentUser.id);

                        int rows = insertStmt.executeUpdate();
                        if (rows > 0) {
                            messageLabel.setText("✅ Заявки отправлены (" + rows + " пользователям)");
                            loadRequests();
                            loadSentRequests();
                        } else {
                            messageLabel.setText("⚠️ Не удалось отправить заявки");
                        }
                    } catch (Exception e) {
                        messageLabel.setText("❌ Ошибка: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            messageLabel.setText("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
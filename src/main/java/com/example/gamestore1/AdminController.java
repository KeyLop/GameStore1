package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminController {

    @FXML private TableView<Game> gamesTable;
    @FXML private TextField titleField;
    @FXML private TextField priceField;
    @FXML private TextField descField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Колонки создаются в FXML через PropertyValueFactory
        // Ничего дополнительно не нужно
        loadGames();
    }

    private void loadGames() {
        try {
            Connection conn = Database.get();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM [Игра]");
            gamesTable.getItems().clear();
            while (rs.next()) {
                Game g = new Game();
                g.id = rs.getInt("ID_Игры");
                g.title = rs.getString("Название");
                g.price = rs.getDouble("Базовая_цена");
                g.description = rs.getString("Описание");
                gamesTable.getItems().add(g);
            }
        } catch (Exception e) {
            messageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    public void addGame() {
        String title = titleField.getText();
        String price = priceField.getText();
        String desc = descField.getText();

        if (title.isEmpty() || price.isEmpty()) {
            messageLabel.setText("Заполните название и цену!");
            return;
        }

        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO [Игра] (Название, Описание, Базовая_цена, Дата_выхода) VALUES (?, ?, ?, GETDATE())"
            );
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setDouble(3, Double.parseDouble(price));
            stmt.executeUpdate();
            messageLabel.setText("✅ Игра добавлена!");
            loadGames();
            titleField.clear();
            priceField.clear();
            descField.clear();
        } catch (Exception e) {
            messageLabel.setText("❌ Ошибка: " + e.getMessage());
        }
    }
}
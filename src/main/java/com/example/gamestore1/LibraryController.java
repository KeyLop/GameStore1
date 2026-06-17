/**
 * Класс для подключения к базе данных SQL Server
 */
package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LibraryController {

    @FXML private ListView<Game> libraryListView;
    @FXML private Label countLabel;
    @FXML private TextField searchField;

    private List<Game> library = new ArrayList<>();

    @FXML
    public void initialize() {
        loadLibrary();

        libraryListView.setCellFactory(lv -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setText(null);
                } else {
                    setText(game.title + "  |  ⭐ " + game.rating + "  |  💰 " + game.price + " ₽");
                }
            }
        });
    }

    private void loadLibrary() {
        if (LoginController.currentUser == null) return;

        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT g.* FROM [Игра] g " +
                            "JOIN [СоставЗаказа] sz ON g.ID_Игры = sz.ID_Игры " +
                            "JOIN [Заказ] z ON sz.[ID Заказа] = z.[ID Заказа] " +
                            "WHERE z.ID_Пользователя = ? AND z.Статус = 'COMPLETED'"
            );
            stmt.setInt(1, LoginController.currentUser.id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Game g = new Game();
                g.id = rs.getInt("ID_Игры");
                g.title = rs.getString("Название");
                g.price = rs.getDouble("Базовая_цена");
                library.add(g);
            }

            libraryListView.getItems().addAll(library);
            countLabel.setText("Игр: " + library.size());

        } catch (Exception e) {
            countLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    public void searchGames() {
        String query = searchField.getText().toLowerCase();
        libraryListView.getItems().clear();
        libraryListView.getItems().addAll(
                library.stream().filter(g -> g.title.toLowerCase().contains(query)).toList()
        );
    }
}
package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CatalogController {

    @FXML private ListView<Game> gameListView;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreFilter;

    private List<Game> allGames = new ArrayList<>();
    public static List<CartItem> cart = new ArrayList<>();

    @FXML
    public void initialize() {
        genreFilter.getItems().addAll("Все", "Экшн", "RPG", "Стратегия");
        genreFilter.setValue("Все");

        loadGames();
        setupListView();
    }

    private void setupListView() {
        gameListView.setCellFactory(lv -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    box.setStyle("-fx-padding: 10; -fx-background-color: #3a3a3a; -fx-background-radius: 5;");

                    Label title = new Label(game.title);
                    title.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

                    Label info = new Label("⭐ " + game.rating + "  💰 " + game.price + " ₽");
                    info.setStyle("-fx-text-fill: #888888;");

                    Label dev = new Label(game.developer != null ? game.developer : "");
                    dev.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");

                    Button btn = new Button("В корзину");
                    btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    btn.setOnAction(e -> {
                        // Проверка, есть ли игра в библиотеке
                        if (isInLibrary(game)) {
                            statusLabel.setText("⚠️ Эта игра уже у вас в библиотеке!");
                            return;
                        }
                        cart.add(new CartItem(game));
                        statusLabel.setText("✅ " + game.title + " добавлена в корзину!");
                    });

                    box.getChildren().addAll(title, info, dev, btn);
                    setGraphic(box);
                }
            }
        });
    }

    private boolean isInLibrary(Game game) {
        if (LoginController.currentUser == null) return false;
        try {
            Connection conn = Database.get();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM [СоставЗаказа] sz " +
                            "JOIN [Заказ] z ON sz.[ID Заказа] = z.[ID Заказа] " +
                            "WHERE z.ID_Пользователя = ? AND sz.ID_Игры = ? AND z.Статус = 'COMPLETED'"
            );
            stmt.setInt(1, LoginController.currentUser.id);
            stmt.setInt(2, game.id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadGames() {
        try {
            Connection conn = Database.get();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM [Игра]");
            while (rs.next()) {
                Game g = new Game();
                g.id = rs.getInt("ID_Игры");
                g.title = rs.getString("Название");
                g.description = rs.getString("Описание");
                g.price = rs.getDouble("Базовая_цена");
                g.developer = rs.getString("ID_Разработчика") != null ? "Разработчик" : "";
                g.rating = 0;
                allGames.add(g);
            }
            gameListView.getItems().addAll(allGames);
            statusLabel.setText("Игр: " + allGames.size());
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    public void searchGames() {
        String query = searchField.getText().toLowerCase();
        gameListView.getItems().clear();
        gameListView.getItems().addAll(
                allGames.stream().filter(g -> g.title.toLowerCase().contains(query)).toList()
        );
    }
}
package com.example.gamestore1;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CartController {

    @FXML private ListView<CartItem> cartListView;
    @FXML private Label totalLabel;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        updateCart();
    }

    private void updateCart() {
        cartListView.getItems().clear();
        cartListView.getItems().addAll(CatalogController.cart);

        double total = 0;
        for (CartItem item : CatalogController.cart) {
            total += item.game.price;
        }
        totalLabel.setText(String.format("%.2f ₽", total));
    }

    @FXML
    public void clearCart() {
        CatalogController.cart.clear();
        updateCart();
        messageLabel.setText("✅ Корзина очищена");
    }

    @FXML
    public void checkout() {
        if (CatalogController.cart.isEmpty()) {
            messageLabel.setText("⚠️ Корзина пуста!");
            return;
        }

        if (LoginController.currentUser == null) {
            messageLabel.setText("⚠️ Войдите в систему!");
            return;
        }

        try {
            Connection conn = Database.get();

            // Получаем следующий ID
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT ISNULL(MAX([ID Заказа]), 0) + 1 FROM [Заказ]"
            );
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int orderId = rs.getInt(1);
            rs.close();
            stmt.close();

            // Создаем заказ
            stmt = conn.prepareStatement(
                    "INSERT INTO [Заказ] ([ID Заказа], ID_Пользователя, [Дата и время], [Общая стоимость], Статус) " +
                            "VALUES (?, ?, GETDATE(), ?, 'COMPLETED')"
            );
            stmt.setInt(1, orderId);
            stmt.setInt(2, LoginController.currentUser.id);

            double total = 0;
            for (CartItem item : CatalogController.cart) {
                total += item.game.price;
            }
            stmt.setDouble(3, total);
            stmt.executeUpdate();
            stmt.close();

            // Добавляем игры в состав заказа
            for (CartItem item : CatalogController.cart) {
                stmt = conn.prepareStatement(
                        "INSERT INTO [СоставЗаказа] ([ID Заказа], ID_Игры, [Цена на момент покупки]) " +
                                "VALUES (?, ?, ?)"
                );
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.game.id);
                stmt.setDouble(3, item.game.price);
                stmt.executeUpdate();
                stmt.close();
            }

            CatalogController.cart.clear();
            updateCart();
            messageLabel.setText("✅ Заказ №" + orderId + " оформлен!");

        } catch (Exception e) {
            messageLabel.setText("❌ Ошибка: " + e.getMessage());
        }
    }
}
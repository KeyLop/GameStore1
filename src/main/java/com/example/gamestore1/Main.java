package com.example.gamestore1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/gamestore1/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        // Подключаем CSS
        scene.getStylesheets().add(
                getClass().getResource("/com/example/gamestore1/styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("GameStore");
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
module com.example.gamestore1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gamestore1 to javafx.fxml;
    exports com.example.gamestore1;
}
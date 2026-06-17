package com.example.gamestore1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Database {

    // ===== ИСПРАВЬ ЭТИ ДАННЫЕ =====
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=game shop;encrypt=true;trustServerCertificate=true";

    // ВАРИАНТ 1: Используй game_user (если он есть)
    private static final String USER = "superadmin";
    private static final String PASS = "Super123!";

    // ВАРИАНТ 2: Используй sa (если знаешь пароль)
    // private static final String USER = "sa";
    // private static final String PASS = "твой_пароль_sa";

    // ВАРИАНТ 3: Используй Windows Authentication (без логина/пароля)
    // private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=game shop;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    // тогда НЕ НУЖНЫ USER и PASS

    private static Connection conn;

    public static Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Подключение к БД установлено!");
        }
        return conn;
    }
}
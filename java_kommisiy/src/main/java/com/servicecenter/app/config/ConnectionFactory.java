package com.servicecenter.app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionFactory {

    private static final DatabaseConfig DATABASE_CONFIG = new DatabaseConfig();

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection;
        String user = DATABASE_CONFIG.getUser();
        String password = DATABASE_CONFIG.getPassword();

        if (user.isBlank() && password.isBlank()) {
            connection = DriverManager.getConnection(DATABASE_CONFIG.getUrl());
        } else {
            connection = DriverManager.getConnection(DATABASE_CONFIG.getUrl(), user, password);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}

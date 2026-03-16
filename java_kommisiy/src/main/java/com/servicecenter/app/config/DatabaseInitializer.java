package com.servicecenter.app.config;

import com.servicecenter.app.util.SqlScriptReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public void initializeDatabase() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            executeStatements(connection, SqlScriptReader.readStatements("sql/schema.sql"));
            executeStatements(connection, SqlScriptReader.readStatements("sql/data.sql"));

            connection.commit();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка инициализации базы данных.", exception);
            throw new IllegalStateException("Не удалось инициализировать базу данных.", exception);
        }
    }

    private void executeStatements(Connection connection, List<String> statements) throws SQLException {
        for (String sql : statements) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }
}

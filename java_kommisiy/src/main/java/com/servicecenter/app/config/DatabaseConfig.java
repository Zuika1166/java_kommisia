package com.servicecenter.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    private final Properties properties = new Properties();

    public DatabaseConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Файл db.properties не найден.");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Не удалось загрузить настройки базы данных.", exception);
        }
    }

    public String getUrl() {
        return properties.getProperty("db.url", "").trim();
    }

    public String getUser() {
        return properties.getProperty("db.user", "").trim();
    }

    public String getPassword() {
        return properties.getProperty("db.password", "").trim();
    }
}

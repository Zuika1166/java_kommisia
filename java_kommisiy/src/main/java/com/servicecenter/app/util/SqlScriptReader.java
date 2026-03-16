package com.servicecenter.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SqlScriptReader {

    private SqlScriptReader() {
    }

    public static List<String> readStatements(String resourcePath) {
        try (InputStream inputStream = SqlScriptReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("SQL-скрипт не найден: " + resourcePath);
            }

            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                content = reader.lines()
                        .filter(line -> !line.trim().startsWith("--"))
                        .collect(Collectors.joining("\n"));
            }

            return Arrays.stream(content.split(";"))
                    .map(String::trim)
                    .filter(statement -> !statement.isBlank())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Не удалось прочитать SQL-скрипт: " + resourcePath, exception);
        }
    }
}

package com.servicecenter.app.model;

import java.util.Arrays;

public enum OrderStatus {
    NEW,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public static OrderStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Неизвестный статус. Доступно: " + availableValues()
                ));
    }

    public static String availableValues() {
        return Arrays.toString(values());
    }
}

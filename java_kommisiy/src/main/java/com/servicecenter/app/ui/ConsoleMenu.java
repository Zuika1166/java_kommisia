package com.servicecenter.app.ui;

import com.servicecenter.app.model.Order;
import com.servicecenter.app.model.OrderStatus;
import com.servicecenter.app.model.Part;
import com.servicecenter.app.service.OrderService;
import com.servicecenter.app.service.PartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final PartService partService;
    private final OrderService orderService;

    public ConsoleMenu(PartService partService, OrderService orderService) {
        this.partService = partService;
        this.orderService = orderService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> showAllParts();
                    case "2" -> addPart();
                    case "3" -> updatePart();
                    case "4" -> deletePart();
                    case "5" -> searchParts();
                    case "6" -> showAllOrders();
                    case "7" -> filterOrdersByStatus();
                    case "8" -> placeOrder();
                    case "9" -> updateOrderStatus();
                    case "10" -> deleteOrder();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестный пункт меню. Повторите ввод.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }

        System.out.println("Приложение завершено.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Сервисный центр =====");
        System.out.println("1. Показать все запчасти");
        System.out.println("2. Добавить запчасть");
        System.out.println("3. Обновить запчасть");
        System.out.println("4. Удалить запчасть");
        System.out.println("5. Поиск запчастей по названию");
        System.out.println("6. Показать все заказы");
        System.out.println("7. Фильтрация заказов по статусу");
        System.out.println("8. Оформить заказ на ремонт");
        System.out.println("9. Обновить статус заказа");
        System.out.println("10. Удалить заказ");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт меню: ");
    }

    private void showAllParts() {
        List<Part> parts = partService.getAllParts();
        printParts(parts);
    }

    private void addPart() {
        String name = readRequiredString("Введите название запчасти: ");
        String manufacturer = readRequiredString("Введите производителя: ");
        BigDecimal price = readBigDecimal("Введите цену: ");
        int quantity = readInt("Введите количество на складе: ");

        Part part = partService.addPart(name, manufacturer, price, quantity);
        System.out.println("Запчасть добавлена: " + part);
    }

    private void updatePart() {
        long id = readLong("Введите ID запчасти для обновления: ");
        Optional<Part> existingPart = partService.getPartById(id);
        if (existingPart.isEmpty()) {
            System.out.println("Запчасть не найдена.");
            return;
        }

        String name = readRequiredString("Введите новое название запчасти: ");
        String manufacturer = readRequiredString("Введите нового производителя: ");
        BigDecimal price = readBigDecimal("Введите новую цену: ");
        int quantity = readInt("Введите новое количество: ");

        boolean updated = partService.updatePart(id, name, manufacturer, price, quantity);
        System.out.println(updated ? "Запчасть обновлена." : "Не удалось обновить запчасть.");
    }

    private void deletePart() {
        long id = readLong("Введите ID запчасти для удаления: ");
        boolean deleted = partService.deletePart(id);
        System.out.println(deleted ? "Запчасть удалена." : "Запчасть не найдена.");
    }

    private void searchParts() {
        String searchTerm = readRequiredString("Введите часть названия запчасти: ");
        List<Part> parts = partService.searchByName(searchTerm);
        printParts(parts);
    }

    private void showAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        printOrders(orders);
    }

    private void filterOrdersByStatus() {
        OrderStatus status = readOrderStatus();
        List<Order> orders = orderService.getOrdersByStatus(status);
        printOrders(orders);
    }

    private void placeOrder() {
        String customerName = readRequiredString("Введите имя клиента: ");
        String deviceModel = readRequiredString("Введите модель устройства: ");
        long partId = readLong("Введите ID запчасти: ");
        int quantity = readInt("Введите количество запчастей для заказа: ");

        Order order = orderService.placeOrder(customerName, deviceModel, partId, quantity);
        System.out.println("Заказ успешно оформлен: " + order);
    }

    private void updateOrderStatus() {
        long orderId = readLong("Введите ID заказа: ");
        OrderStatus status = readOrderStatus();

        boolean updated = orderService.updateOrderStatus(orderId, status);
        System.out.println(updated ? "Статус заказа обновлен." : "Заказ не найден.");
    }

    private void deleteOrder() {
        long orderId = readLong("Введите ID заказа для удаления: ");
        boolean deleted = orderService.deleteOrder(orderId);
        System.out.println(deleted ? "Заказ удален." : "Заказ не найден.");
    }

    private void printParts(List<Part> parts) {
        if (parts.isEmpty()) {
            System.out.println("Список запчастей пуст.");
            return;
        }

        System.out.println("----- Запчасти -----");
        parts.forEach(System.out::println);
    }

    private void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("Список заказов пуст.");
            return;
        }

        System.out.println("----- Заказы -----");
        orders.forEach(System.out::println);
    }

    private String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Поле не должно быть пустым.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите корректный ID.");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().replace(',', '.');
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите корректное число.");
            }
        }
    }

    private OrderStatus readOrderStatus() {
        while (true) {
            System.out.print("Введите статус " + OrderStatus.availableValues() + ": ");
            String value = scanner.nextLine().trim();
            try {
                return OrderStatus.fromString(value);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}

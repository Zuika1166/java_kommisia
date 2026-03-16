package com.servicecenter.app.service;

import com.servicecenter.app.config.ConnectionFactory;
import com.servicecenter.app.dao.impl.OrderDaoImpl;
import com.servicecenter.app.dao.impl.PartDaoImpl;
import com.servicecenter.app.model.Order;
import com.servicecenter.app.model.OrderStatus;
import com.servicecenter.app.model.Part;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    private final OrderDaoImpl orderDao;
    private final PartDaoImpl partDao;

    public OrderService(OrderDaoImpl orderDao, PartDaoImpl partDao) {
        this.orderDao = orderDao;
        this.partDao = partDao;
    }

    public Order placeOrder(String customerName, String deviceModel, long partId, int quantity) {
        validateOrderData(customerName, deviceModel, quantity);

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Part part = partDao.findById(connection, partId)
                        .orElseThrow(() -> new IllegalArgumentException("Запчасть с указанным ID не найдена."));

                if (part.getQuantity() < quantity) {
                    throw new IllegalStateException("Недостаточно запчастей на складе для оформления заказа.");
                }

                part.setQuantity(part.getQuantity() - quantity);
                partDao.update(connection, part);

                BigDecimal totalPrice = part.getPrice().multiply(BigDecimal.valueOf(quantity));
                Order order = new Order(
                        null,
                        customerName.trim(),
                        deviceModel.trim(),
                        partId,
                        quantity,
                        totalPrice,
                        OrderStatus.NEW,
                        LocalDateTime.now().withNano(0)
                );

                Order createdOrder = orderDao.create(connection, order);
                connection.commit();
                return createdOrder;
            } catch (SQLException | RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при транзакционном оформлении заказа.", exception);
            throw new IllegalStateException("Не удалось оформить заказ.", exception);
        }
    }

    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    public Optional<Order> getOrderById(long id) {
        return orderDao.findById(id);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderDao.findByStatus(status);
    }

    public boolean updateOrderStatus(long orderId, OrderStatus status) {
        Optional<Order> existingOrder = orderDao.findById(orderId);
        if (existingOrder.isEmpty()) {
            return false;
        }

        Order order = existingOrder.get();
        order.setStatus(status);
        return orderDao.update(order);
    }

    public boolean deleteOrder(long orderId) {
        return orderDao.delete(orderId);
    }

    private void validateOrderData(String customerName, String deviceModel, int quantity) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Имя клиента не должно быть пустым.");
        }
        if (deviceModel == null || deviceModel.isBlank()) {
            throw new IllegalArgumentException("Модель устройства не должна быть пустой.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество в заказе должно быть больше нуля.");
        }
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Не удалось выполнить откат транзакции.", exception);
        }
    }
}

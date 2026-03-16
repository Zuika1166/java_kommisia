package com.servicecenter.app.dao.impl;

import com.servicecenter.app.config.ConnectionFactory;
import com.servicecenter.app.dao.OrderDao;
import com.servicecenter.app.model.Order;
import com.servicecenter.app.model.OrderStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDaoImpl implements OrderDao {

    private static final Logger LOGGER = Logger.getLogger(OrderDaoImpl.class.getName());

    private static final String INSERT_SQL =
            "INSERT INTO repair_orders (customer_name, device_model, part_id, quantity, total_price, status, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, customer_name, device_model, part_id, quantity, total_price, status, created_at "
                    + "FROM repair_orders WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, customer_name, device_model, part_id, quantity, total_price, status, created_at "
                    + "FROM repair_orders ORDER BY created_at DESC";
    private static final String UPDATE_SQL =
            "UPDATE repair_orders SET customer_name = ?, device_model = ?, part_id = ?, quantity = ?, total_price = ?, status = ?, created_at = ? "
                    + "WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM repair_orders WHERE id = ?";
    private static final String FIND_BY_STATUS_SQL =
            "SELECT id, customer_name, device_model, part_id, quantity, total_price, status, created_at "
                    + "FROM repair_orders WHERE status = ? ORDER BY created_at DESC";

    @Override
    public Order create(Order order) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return create(connection, order);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при создании заказа.", exception);
            throw new IllegalStateException("Не удалось создать заказ.", exception);
        }
    }

    public Order create(Connection connection, Order order) throws SQLException {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, order.getCustomerName());
            preparedStatement.setString(2, order.getDeviceModel());
            preparedStatement.setLong(3, order.getPartId());
            preparedStatement.setInt(4, order.getQuantity());
            preparedStatement.setBigDecimal(5, order.getTotalPrice());
            preparedStatement.setString(6, order.getStatus().name());
            preparedStatement.setString(7, order.getCreatedAt().toString());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                }
            }
            return order;
        }
    }

    @Override
    public Optional<Order> findById(long id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return findById(connection, id);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении заказа по ID.", exception);
            throw new IllegalStateException("Не удалось получить заказ.", exception);
        }
    }

    public Optional<Order> findById(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                orders.add(mapRow(resultSet));
            }
            return orders;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении списка заказов.", exception);
            throw new IllegalStateException("Не удалось получить список заказов.", exception);
        }
    }

    @Override
    public boolean update(Order order) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return update(connection, order);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при обновлении заказа.", exception);
            throw new IllegalStateException("Не удалось обновить заказ.", exception);
        }
    }

    public boolean update(Connection connection, Order order) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, order.getCustomerName());
            preparedStatement.setString(2, order.getDeviceModel());
            preparedStatement.setLong(3, order.getPartId());
            preparedStatement.setInt(4, order.getQuantity());
            preparedStatement.setBigDecimal(5, order.getTotalPrice());
            preparedStatement.setString(6, order.getStatus().name());
            preparedStatement.setString(7, order.getCreatedAt().toString());
            preparedStatement.setLong(8, order.getId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при удалении заказа.", exception);
            throw new IllegalStateException("Не удалось удалить заказ.", exception);
        }
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_STATUS_SQL)) {
            preparedStatement.setString(1, status.name());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapRow(resultSet));
                }
            }
            return orders;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при фильтрации заказов по статусу.", exception);
            throw new IllegalStateException("Не удалось отфильтровать заказы.", exception);
        }
    }

    private Order mapRow(ResultSet resultSet) throws SQLException {
        return new Order(
                resultSet.getLong("id"),
                resultSet.getString("customer_name"),
                resultSet.getString("device_model"),
                resultSet.getLong("part_id"),
                resultSet.getInt("quantity"),
                new BigDecimal(resultSet.getString("total_price")),
                OrderStatus.fromString(resultSet.getString("status")),
                LocalDateTime.parse(resultSet.getString("created_at"))
        );
    }
}

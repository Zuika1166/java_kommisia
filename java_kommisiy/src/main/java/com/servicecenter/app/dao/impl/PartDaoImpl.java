package com.servicecenter.app.dao.impl;

import com.servicecenter.app.config.ConnectionFactory;
import com.servicecenter.app.dao.PartDao;
import com.servicecenter.app.model.Part;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PartDaoImpl implements PartDao {

    private static final Logger LOGGER = Logger.getLogger(PartDaoImpl.class.getName());

    private static final String INSERT_SQL =
            "INSERT INTO parts (name, manufacturer, price, quantity) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, manufacturer, price, quantity FROM parts WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, name, manufacturer, price, quantity FROM parts ORDER BY id";
    private static final String UPDATE_SQL =
            "UPDATE parts SET name = ?, manufacturer = ?, price = ?, quantity = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM parts WHERE id = ?";
    private static final String SEARCH_BY_NAME_SQL =
            "SELECT id, name, manufacturer, price, quantity FROM parts WHERE name LIKE ? ORDER BY name";

    @Override
    public Part create(Part part) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return create(connection, part);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при добавлении запчасти.", exception);
            throw new IllegalStateException("Не удалось добавить запчасть.", exception);
        }
    }

    public Part create(Connection connection, Part part) throws SQLException {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, part.getName());
            preparedStatement.setString(2, part.getManufacturer());
            preparedStatement.setBigDecimal(3, part.getPrice());
            preparedStatement.setInt(4, part.getQuantity());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    part.setId(generatedKeys.getLong(1));
                }
            }
            return part;
        }
    }

    @Override
    public Optional<Part> findById(long id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return findById(connection, id);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении запчасти по ID.", exception);
            throw new IllegalStateException("Не удалось получить запчасть.", exception);
        }
    }

    public Optional<Part> findById(Connection connection, long id) throws SQLException {
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
    public List<Part> findAll() {
        List<Part> parts = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                parts.add(mapRow(resultSet));
            }
            return parts;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении списка запчастей.", exception);
            throw new IllegalStateException("Не удалось получить список запчастей.", exception);
        }
    }

    @Override
    public boolean update(Part part) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return update(connection, part);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при обновлении запчасти.", exception);
            throw new IllegalStateException("Не удалось обновить запчасть.", exception);
        }
    }

    public boolean update(Connection connection, Part part) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, part.getName());
            preparedStatement.setString(2, part.getManufacturer());
            preparedStatement.setBigDecimal(3, part.getPrice());
            preparedStatement.setInt(4, part.getQuantity());
            preparedStatement.setLong(5, part.getId());
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
            LOGGER.log(Level.SEVERE, "Ошибка при удалении запчасти.", exception);
            throw new IllegalStateException("Не удалось удалить запчасть.", exception);
        }
    }

    @Override
    public List<Part> searchByName(String name) {
        List<Part> parts = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_BY_NAME_SQL)) {
            preparedStatement.setString(1, "%" + name.trim() + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parts.add(mapRow(resultSet));
                }
            }
            return parts;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Ошибка при поиске запчастей по названию.", exception);
            throw new IllegalStateException("Не удалось выполнить поиск запчастей.", exception);
        }
    }

    private Part mapRow(ResultSet resultSet) throws SQLException {
        return new Part(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("manufacturer"),
                new BigDecimal(resultSet.getString("price")),
                resultSet.getInt("quantity")
        );
    }
}

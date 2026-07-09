package com.ust.sdet.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderRepository {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public OrderRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public long save(Order order) {
        String orderSql = """
                INSERT INTO retail_orders(status, total_paise, ordered_on, refunded)
                VALUES (?, ?, ?, ?)
                """;
        String itemSql = """
                INSERT INTO retail_order_items(order_id, sku, quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = connection()) {
            connection.setAutoCommit(false);
            long orderId;
            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStatement.setString(1, order.status());
                orderStatement.setLong(2, order.totalPaise());
                orderStatement.setDate(3, Date.valueOf(order.orderedOn()));
                orderStatement.setBoolean(4, order.refunded());
                orderStatement.executeUpdate();
                orderId = generatedId(orderStatement);
            }

            try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                itemStatement.setLong(1, orderId);
                itemStatement.setString(2, order.sku());
                itemStatement.setInt(3, order.quantity());
                itemStatement.executeUpdate();
            }

            connection.commit();
            return orderId;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save order test data", e);
        }
    }

    public int count() {
        return queryForInt("SELECT COUNT(*) FROM retail_orders");
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM retail_orders WHERE status = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not count orders by status", e);
        }
    }

    public int referenceStatusCount() {
        return queryForInt("SELECT COUNT(*) FROM order_statuses");
    }

    public void resetMutableTables() {
        String sql = "TRUNCATE retail_order_items, retail_orders RESTART IDENTITY CASCADE";
        try (Connection connection = connection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not reset order test data", e);
        }
    }

    private int queryForInt(String sql) {
        try (Connection connection = connection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not run count query", e);
        }
    }

    private long generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("Insert did not return a generated id");
            }
            return keys.getLong(1);
        }
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}

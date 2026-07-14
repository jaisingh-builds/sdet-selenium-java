package com.ust.sdet.w7d1.db;

import com.ust.sdet.w7d1.runtime.W7D1Runtime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDatabase {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public OrderDatabase(W7D1Runtime runtime) {
        this.jdbcUrl = runtime.jdbcUrl();
        this.username = runtime.databaseUsername();
        this.password = runtime.databasePassword();
    }

    public OrderRow find(long orderId) {
        String sql = """
            SELECT status, subtotal_paise, discount_paise, total_paise, coupon_code
            FROM orders
            WHERE id = ?
            """;
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new AssertionError("Order row " + orderId + " was not persisted");
                }
                return new OrderRow(
                    rows.getString("status"),
                    rows.getLong("subtotal_paise"),
                    rows.getLong("discount_paise"),
                    rows.getLong("total_paise"),
                    rows.getString("coupon_code")
                );
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Could not query the ShopKart order row", error);
        }
    }

    public void cleanupCart(Long cartId) {
        if (cartId == null) {
            return;
        }
        try (Connection connection = open()) {
            connection.setAutoCommit(false);
            try {
                execute(
                    connection,
                    "DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE cart_id = ?)",
                    cartId
                );
                execute(connection, "DELETE FROM orders WHERE cart_id = ?", cartId);
                execute(connection, "DELETE FROM cart_items WHERE cart_id = ?", cartId);
                execute(connection, "DELETE FROM carts WHERE id = ?", cartId);
                connection.commit();
            } catch (SQLException error) {
                connection.rollback();
                throw error;
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Could not clean W7D1 scenario data", error);
        }
    }

    private Connection open() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private static void execute(Connection connection, String sql, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}

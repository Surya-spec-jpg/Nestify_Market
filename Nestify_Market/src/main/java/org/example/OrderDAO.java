package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final Connection conn;

    public OrderDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean createOrder(Order order) throws SQLException {
        String query = "INSERT INTO orders (buyer_id, product_id, status, total_price, delivery_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getBuyerId());
            stmt.setInt(2, order.getProductId());
            stmt.setString(3, order.getStatus());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setString(5, order.getDeliveryStatus()); // Set delivery status
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) return false;

            // Capture generated order_id back into the Order object
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setOrderId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateDeliveryStatus(int orderId, String deliveryStatus) throws SQLException {
        String sql = "UPDATE orders SET delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deliveryStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Order> getOrdersByBuyer(int buyerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT order_id, buyer_id, product_id, status, total_price, delivery_status FROM orders WHERE buyer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("buyer_id"),
                            rs.getInt("product_id"),
                            rs.getString("status"),
                            rs.getDouble("total_price"),
                            rs.getString("delivery_status") // Get delivery status
                    );
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT order_id, buyer_id, product_id, status, total_price, delivery_status FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getInt("buyer_id"),
                        rs.getInt("product_id"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getString("delivery_status") // Return delivery status
                );
            }
        }
        return null; // not found
    }
}

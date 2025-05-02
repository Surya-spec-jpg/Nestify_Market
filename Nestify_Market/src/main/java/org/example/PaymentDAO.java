package org.example;

import java.sql.*;

public class PaymentDAO {
    private final Connection conn;

    public PaymentDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Creates a new payment record in PENDING status.
     */
    public boolean createPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (order_id, payment_status) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getOrderId());
            ps.setString(2, payment.getStatus());  // e.g. "pending"
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    payment.setPaymentId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    /**
     * Updates only the payment_status column.
     */
    public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        }
    }
}

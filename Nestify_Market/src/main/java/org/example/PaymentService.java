package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final OrderDAO orderDAO;

    public PaymentService(Connection conn) {
        this.paymentDAO = new PaymentDAO(conn);
        this.orderDAO = new OrderDAO(conn);
    }

    /**
     * Process payment for an order.
     * - Creates a PENDING payment record.
     * - Simulates a gateway charge.
     * - Updates payment_status and order status accordingly.
     */
    public boolean processPayment(int orderId) {
        try {
            // 1) create a pending payment record
            Payment payment = new Payment(0, orderId, "pending", 5.00);
            if (!paymentDAO.createPayment(payment)) return false;

            // 2) simulate gateway logic (90% success rate)
            boolean success = Math.random() < 0.9;

            // 3) update payment_status
            String payStatus = success ? "successful" : "failed";
            paymentDAO.updatePaymentStatus(payment.getPaymentId(), payStatus);

            // 4) update order status
            String orderStatus = success ? "completed" : "cancelled";
            orderDAO.updateOrderStatus(orderId, orderStatus);

            return success;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                orderDAO.updateOrderStatus(orderId, "cancelled");
            } catch (Exception ignored) {}
            return false;
        }
    }

    /**
     * Process commission payment only after delivery confirmation.
     * - Commission is 5% of the total order amount.
     */
    public boolean processCommission(int orderId) {
        try {
            Order order = orderDAO.getOrderById(orderId);
            if (order == null || !"delivered".equals(order.getDeliveryStatus())) {
                return false;
            }
            double commission = order.getTotalPrice() * 0.05;
            Payment payment = new Payment(0, orderId, "pending", 5.00);
            if (!paymentDAO.createPayment(payment)) return false;

            // simulate gateway
            boolean success = Math.random() < 0.95;
            String status = success ? "successful" : "failed";
            paymentDAO.updatePaymentStatus(payment.getPaymentId(), status);

            if (success) orderDAO.updateOrderStatus(orderId, "completed");
            else       orderDAO.updateOrderStatus(orderId, "cancelled");

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

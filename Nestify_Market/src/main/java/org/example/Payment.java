package org.example;

public class Payment {
    private int paymentId;
    private int orderId;
    private String status;               // maps to payment_status
    private double commissionPercentage; // maps to commission_percentage

    public Payment() {}

    public Payment(int paymentId, int orderId, String status, double commissionPercentage) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.status = status;
        this.commissionPercentage = commissionPercentage;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(double commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }
}

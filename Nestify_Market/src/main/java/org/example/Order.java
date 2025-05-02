package org.example;

public class Order {
    private int orderId;
    private int buyerId;
    private int productId;
    private String status;
    private double totalPrice; // This stores the total price of the order
    private String deliveryStatus; // Added field for tracking delivery status

    // Constructor
    public Order(int orderId, int buyerId, int productId, String status, double totalPrice, String deliveryStatus) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getDeliveryStatus() { return deliveryStatus; } // Getter for delivery status
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; } // Setter for delivery status
}

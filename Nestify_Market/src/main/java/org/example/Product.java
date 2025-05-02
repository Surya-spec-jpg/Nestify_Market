package org.example;

public class Product {
    private int productId;
    private int sellerId;
    private String name;
    private String description;
    private double price;
    private String status;  // 'new' or 'second_hand'

    // Default constructor
    public Product() {}

    // All-args constructor (no imageUrl)
    public Product(int productId, int sellerId, String name, String description, double price, String status) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    // Getters & setters (omit any for imageUrl)
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

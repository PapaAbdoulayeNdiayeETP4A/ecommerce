// models/CartItem.java
package com.example.ecommerce.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String productId;
    private String userId;
    private int quantity;
    private double price;

    public CartItem() {}

    public CartItem(String productId, String userId, int quantity, double price) {
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Méthode utilitaire pour calculer le sous-total
    public double getSubtotal() {
        return price * quantity;
    }
}
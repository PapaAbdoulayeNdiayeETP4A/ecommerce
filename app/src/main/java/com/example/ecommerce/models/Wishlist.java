package com.example.ecommerce.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wishlist_items")
public class Wishlist {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String userId;
    private String productId;
    private long dateAdded;

    public Wishlist() {
    }

    public Wishlist(String userId, String productId, long dateAdded) {
        this.userId = userId;
        this.productId = productId;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
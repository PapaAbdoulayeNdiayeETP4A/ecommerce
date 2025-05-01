package com.example.ecommerce.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "payment_methods")
public class PaymentMethod {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String userId;
    private String type; // "credit_card", "paypal", etc.
    private String cardNumber; // Masqué (par exemple, "XXXX XXXX XXXX 1234")
    private String cardHolderName;
    private String expiryDate;
    private boolean isDefault;

    public PaymentMethod() {
    }

    public PaymentMethod(String userId, String type, String cardNumber, String cardHolderName, String expiryDate) {
        this.userId = userId;
        this.type = type;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.isDefault = false;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
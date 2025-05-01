package com.example.ecommerce.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "reviews")
public class Review {
    @PrimaryKey
    private String reviewId;
    private String productId;
    private String userId;
    private String userDisplayName;
    private String userProfileImageUrl;
    private String text;
    private float rating;
    private Date date;
    private boolean verified;

    public Review() {
    }

    public Review(String reviewId, String productId, String userId, String userDisplayName, String text, float rating, Date date) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.userDisplayName = userDisplayName;
        this.text = text;
        this.rating = rating;
        this.date = date;
        this.verified = false;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Review review);

    @Update
    void update(Review review);

    @Delete
    void delete(Review review);

    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY date DESC")
    LiveData<List<Review>> getReviewsByProduct(String productId);

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Review>> getReviewsByUser(String userId);

    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId")
    LiveData<Review> getReviewById(String reviewId);

    @Query("SELECT COUNT(*) FROM reviews WHERE productId = :productId")
    LiveData<Integer> getReviewCountForProduct(String productId);

    @Query("SELECT AVG(rating) FROM reviews WHERE productId = :productId")
    LiveData<Float> getAverageRatingForProduct(String productId);
}
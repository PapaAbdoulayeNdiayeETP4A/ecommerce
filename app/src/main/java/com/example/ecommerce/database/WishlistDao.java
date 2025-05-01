package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ecommerce.models.Wishlist;

import java.util.List;

@Dao
public interface WishlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Wishlist wishlistItem);

    @Delete
    void delete(Wishlist wishlistItem);

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId ORDER BY dateAdded DESC")
    LiveData<List<Wishlist>> getWishlistByUser(String userId);

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    LiveData<Wishlist> getWishlistItem(String userId, String productId);

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    void deleteByProductId(String userId, String productId);

    @Query("SELECT COUNT(*) FROM wishlist_items WHERE userId = :userId")
    LiveData<Integer> getWishlistCount(String userId);

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND productId = :productId LIMIT 1)")
    LiveData<Boolean> isInWishlist(String userId, String productId);
}
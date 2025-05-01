package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.CartItem;

import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    LiveData<List<CartItem>> getCartItems(String userId);

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    LiveData<CartItem> getCartItemByProductId(String userId, String productId);

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCart(String userId);

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE userId = :userId")
    LiveData<Double> getCartTotal(String userId);

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    LiveData<Integer> getCartItemCount(String userId);
}
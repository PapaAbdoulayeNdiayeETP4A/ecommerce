package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.OrderItem;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderItems(List<OrderItem> orderItems);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    LiveData<List<Order>> getOrdersByUser(String userId);

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    LiveData<Order> getOrderById(String orderId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    LiveData<List<OrderItem>> getOrderItems(String orderId);

    @Transaction
    default void insertOrderWithItems(Order order, List<OrderItem> items) {
        insert(order);
        insertOrderItems(items);
    }
}
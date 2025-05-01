package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Update
    void update(Product product);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE productId = :productId")
    LiveData<Product> getProductById(String productId);

    @Query("SELECT * FROM products WHERE category = :category")
    LiveData<List<Product>> getProductsByCategory(String category);

    @Query("SELECT * FROM products WHERE isFeatured = 1")
    LiveData<List<Product>> getFeaturedProducts();

    @Query("SELECT * FROM products WHERE isOnSale = 1")
    LiveData<List<Product>> getProductsOnSale();

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<Product>> searchProducts(String query);
}
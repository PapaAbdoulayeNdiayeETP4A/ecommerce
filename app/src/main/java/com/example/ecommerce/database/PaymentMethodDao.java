package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.PaymentMethod;

import java.util.List;

@Dao
public interface PaymentMethodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PaymentMethod paymentMethod);

    @Update
    void update(PaymentMethod paymentMethod);

    @Delete
    void delete(PaymentMethod paymentMethod);

    @Query("SELECT * FROM payment_methods WHERE userId = :userId ORDER BY isDefault DESC")
    LiveData<List<PaymentMethod>> getPaymentMethodsByUser(String userId);

    @Query("SELECT * FROM payment_methods WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<PaymentMethod> getDefaultPaymentMethod(String userId);

    @Query("SELECT * FROM payment_methods WHERE id = :paymentMethodId")
    LiveData<PaymentMethod> getPaymentMethodById(long paymentMethodId);

    @Query("UPDATE payment_methods SET isDefault = 0 WHERE userId = :userId")
    void resetDefaultPaymentMethods(String userId);

    @Query("UPDATE payment_methods SET isDefault = 1 WHERE id = :paymentMethodId")
    void setDefaultPaymentMethod(long paymentMethodId);
}
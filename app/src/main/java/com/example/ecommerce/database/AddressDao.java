package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.Address;

import java.util.List;

@Dao
public interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Address address);

    @Update
    void update(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC")
    LiveData<List<Address>> getAddressesByUser(String userId);

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    LiveData<Address> getDefaultAddress(String userId);

    @Query("SELECT * FROM addresses WHERE id = :addressId")
    LiveData<Address> getAddressById(long addressId);

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    void resetDefaultAddresses(String userId);

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :addressId")
    void setDefaultAddress(long addressId);
}
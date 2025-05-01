package com.example.ecommerce.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.models.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<Notification>> getNotificationsByUser(String userId);

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    LiveData<List<Notification>> getUnreadNotifications(String userId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    LiveData<Integer> getUnreadNotificationCount(String userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsRead(String userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(long notificationId);

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteAllNotifications(String userId);
}
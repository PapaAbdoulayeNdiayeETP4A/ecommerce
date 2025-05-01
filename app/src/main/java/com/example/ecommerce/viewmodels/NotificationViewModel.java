// viewmodels/NotificationViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.models.Notification;
import com.example.ecommerce.repositories.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private NotificationRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public NotificationViewModel(Application application) {
        super(application);
        repository = new NotificationRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir les notifications de l'utilisateur
    public LiveData<List<Notification>> getUserNotifications() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserNotifications(userId)
        );
    }

    // Obtenir les notifications non lues
    public LiveData<List<Notification>> getUnreadNotifications() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUnreadNotifications(userId)
        );
    }

    // Obtenir le nombre de notifications non lues
    public LiveData<Integer> getUnreadNotificationCount() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUnreadNotificationCount(userId)
        );
    }

    // Marquer une notification comme lue
    public void markAsRead(long notificationId) {
        repository.markAsRead(notificationId);
    }

    // Marquer toutes les notifications comme lues
    public void markAllAsRead() {
        if (currentUserId.getValue() != null) {
            repository.markAllAsRead(currentUserId.getValue());
        }
    }

    // Supprimer toutes les notifications
    public void deleteAllNotifications() {
        if (currentUserId.getValue() != null) {
            repository.deleteAllNotifications(currentUserId.getValue());
        }
    }
}
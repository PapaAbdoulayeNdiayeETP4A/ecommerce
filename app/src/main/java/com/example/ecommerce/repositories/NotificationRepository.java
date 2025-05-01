package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.NotificationDao;
import com.example.ecommerce.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    private NotificationDao notificationDao;
    private ApiService apiService;

    public NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        notificationDao = db.notificationDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir les notifications d'un utilisateur
    public LiveData<List<Notification>> getUserNotifications(String userId) {
        refreshNotifications(userId);
        return notificationDao.getNotificationsByUser(userId);
    }

    // Méthode pour obtenir les notifications non lues d'un utilisateur
    public LiveData<List<Notification>> getUnreadNotifications(String userId) {
        return notificationDao.getUnreadNotifications(userId);
    }

    // Méthode pour obtenir le nombre de notifications non lues
    public LiveData<Integer> getUnreadNotificationCount(String userId) {
        return notificationDao.getUnreadNotificationCount(userId);
    }

    // Méthode pour marquer une notification comme lue
    public void markAsRead(long notificationId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            notificationDao.markAsRead(notificationId);
        });

        // Appel API pour marquer comme lu
        // Implémentation à adapter selon votre API
    }

    // Méthode pour marquer toutes les notifications comme lues
    public void markAllAsRead(String userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            notificationDao.markAllAsRead(userId);
        });

        // Appel API pour marquer tout comme lu
        Call<Void> call = apiService.markAllNotificationsAsRead(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Échec de la mise à jour des notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Échec de la connexion: " + t.getMessage());
            }
        });
    }

    // Méthode pour supprimer toutes les notifications
    public void deleteAllNotifications(String userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            notificationDao.deleteAllNotifications(userId);
        });

        // Appel API pour supprimer toutes les notifications
        Call<Void> call = apiService.deleteAllNotifications(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Échec de la suppression des notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Échec de la connexion: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les notifications depuis le serveur
    private void refreshNotifications(String userId) {
        // Appel API pour récupérer les notifications
        Call<List<Notification>> call = apiService.getUserNotifications(userId);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (Notification notification : notifications) {
                            notificationDao.insert(notification);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des notifications: " + t.getMessage());
            }
        });
    }
}
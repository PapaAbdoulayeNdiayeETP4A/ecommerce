package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.OrderDao;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.OrderItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private OrderDao orderDao;
    private ApiService apiService;
    private MutableLiveData<ApiResponse<Order>> orderOperationResponse = new MutableLiveData<>();

    public OrderRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        orderDao = db.orderDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour passer une commande
    public LiveData<ApiResponse<Order>> placeOrder(Order order) {
        apiService.placeOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order placedOrder = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        orderDao.insertOrderWithItems(placedOrder, placedOrder.getItems());
                    });
                    orderOperationResponse.setValue(new ApiResponse<>(placedOrder));
                } else {
                    orderOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                orderOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la création de la commande: " + t.getMessage());
            }
        });
        return orderOperationResponse;
    }

    // Méthode pour obtenir les commandes d'un utilisateur
    public LiveData<List<Order>> getUserOrders(String userId) {
        refreshUserOrders(userId);
        return orderDao.getOrdersByUser(userId);
    }

    // Méthode pour obtenir les détails d'une commande
    public LiveData<Order> getOrderDetails(String orderId) {
        refreshOrderDetails(orderId);
        return orderDao.getOrderById(orderId);
    }

    // Méthode pour obtenir les articles d'une commande
    public LiveData<List<OrderItem>> getOrderItems(String orderId) {
        return orderDao.getOrderItems(orderId);
    }

    // Méthode pour rafraîchir les commandes d'un utilisateur
    private void refreshUserOrders(String userId) {
        apiService.getUserOrders(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (Order order : orders) {
                            orderDao.insert(order);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des commandes: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les détails d'une commande
    private void refreshOrderDetails(String orderId) {
        apiService.getOrderDetails(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        orderDao.insertOrderWithItems(order, order.getItems());
                    });
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des détails de la commande: " + t.getMessage());
            }
        });
    }
}
// viewmodels/OrderViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.OrderItem;
import com.example.ecommerce.repositories.OrderRepository;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private OrderRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private MutableLiveData<String> selectedOrderId = new MutableLiveData<>();

    public OrderViewModel(Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Définir la commande sélectionnée
    public void setSelectedOrder(String orderId) {
        selectedOrderId.setValue(orderId);
    }

    // Obtenir les commandes de l'utilisateur
    public LiveData<List<Order>> getUserOrders() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserOrders(userId)
        );
    }

    // Obtenir les détails d'une commande
    public LiveData<Order> getOrderDetails() {
        return Transformations.switchMap(selectedOrderId, orderId ->
                repository.getOrderDetails(orderId)
        );
    }

    // Obtenir les articles d'une commande
    public LiveData<List<OrderItem>> getOrderItems() {
        return Transformations.switchMap(selectedOrderId, orderId ->
                repository.getOrderItems(orderId)
        );
    }

    // Passer une commande
    public LiveData<ApiResponse<Order>> placeOrder(Order order) {
        return repository.placeOrder(order);
    }
}
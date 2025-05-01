// viewmodels/OrderDetailsViewModel.java
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

public class OrderDetailsViewModel extends AndroidViewModel {
    private OrderRepository repository;
    private MutableLiveData<String> selectedOrderId = new MutableLiveData<>();

    public OrderDetailsViewModel(Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    // Définir la commande sélectionnée
    public void setSelectedOrder(String orderId) {
        selectedOrderId.setValue(orderId);
    }

    // Obtenir les détails de la commande sélectionnée
    public LiveData<Order> getOrderDetails() {
        return Transformations.switchMap(selectedOrderId, orderId ->
                repository.getOrderDetails(orderId)
        );
    }

    // Obtenir les articles de la commande sélectionnée
    public LiveData<List<OrderItem>> getOrderItems() {
        return Transformations.switchMap(selectedOrderId, orderId ->
                repository.getOrderItems(orderId)
        );
    }

    // Annuler la commande
    public LiveData<ApiResponse<Order>> cancelOrder() {
        if (selectedOrderId.getValue() != null) {
            return repository.cancelOrder(selectedOrderId.getValue());
        }

        MutableLiveData<ApiResponse<Order>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Commande non définie"));
        return response;
    }

    // Demander un retour/remboursement
    public LiveData<ApiResponse<Order>> requestRefund(String reason) {
        if (selectedOrderId.getValue() != null) {
            return repository.requestRefund(selectedOrderId.getValue(), reason);
        }

        MutableLiveData<ApiResponse<Order>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Commande non définie"));
        return response;
    }
}
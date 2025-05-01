// viewmodels/CartViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.repositories.CartRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public CartViewModel(Application application) {
        super(application);
        repository = new CartRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir les articles du panier
    public LiveData<List<CartItem>> getCartItems() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getCartItems(userId)
        );
    }

    // Obtenir le total du panier
    public LiveData<Double> getCartTotal() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getCartTotal(userId)
        );
    }

    // Obtenir le nombre d'articles dans le panier
    public LiveData<Integer> getCartItemCount() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getCartItemCount(userId)
        );
    }

    // Ajouter un article au panier
    public LiveData<ApiResponse<CartItem>> addToCart(CartItem cartItem) {
        return repository.addToCart(cartItem);
    }

    // Mettre à jour un article du panier
    public LiveData<ApiResponse<CartItem>> updateCartItem(String itemId, CartItem cartItem) {
        return repository.updateCartItem(itemId, cartItem);
    }

    // Supprimer un article du panier
    public LiveData<ApiResponse<Boolean>> removeCartItem(String itemId) {
        return repository.removeCartItem(itemId);
    }

    // Vider le panier
    public void clearCart() {
        if (currentUserId.getValue() != null) {
            repository.clearCart(currentUserId.getValue());
        }
    }
}
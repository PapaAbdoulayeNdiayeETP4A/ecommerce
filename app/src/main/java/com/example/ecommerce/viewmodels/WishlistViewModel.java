// viewmodels/WishlistViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.Wishlist;
import com.example.ecommerce.repositories.WishlistRepository;

import java.util.List;

public class WishlistViewModel extends AndroidViewModel {
    private WishlistRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public WishlistViewModel(Application application) {
        super(application);
        repository = new WishlistRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir la liste de souhaits de l'utilisateur
    public LiveData<List<Wishlist>> getUserWishlist() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserWishlist(userId)
        );
    }

    // Vérifier si un produit est dans la liste de souhaits
    public LiveData<Boolean> isInWishlist(String productId) {
        return Transformations.switchMap(currentUserId, userId ->
                repository.isInWishlist(userId, productId)
        );
    }

    // Obtenir le nombre d'articles dans la liste de souhaits
    public LiveData<Integer> getWishlistCount() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getWishlistCount(userId)
        );
    }

    // Ajouter un produit à la liste de souhaits
    public LiveData<ApiResponse<Boolean>> addToWishlist(String productId) {
        if (currentUserId.getValue() != null) {
            return repository.addToWishlist(currentUserId.getValue(), productId);
        }

        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Utilisateur non défini"));
        return response;
    }

    // Supprimer un produit de la liste de souhaits
    public LiveData<ApiResponse<Boolean>> removeFromWishlist(String productId) {
        if (currentUserId.getValue() != null) {
            return repository.removeFromWishlist(currentUserId.getValue(), productId);
        }

        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Utilisateur non défini"));
        return response;
    }
}
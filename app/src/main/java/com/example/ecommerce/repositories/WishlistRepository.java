package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.WishlistDao;
import com.example.ecommerce.models.Wishlist;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {
    private static final String TAG = "WishlistRepository";
    private WishlistDao wishlistDao;
    private ApiService apiService;
    private MutableLiveData<ApiResponse<Boolean>> wishlistOperationResponse = new MutableLiveData<>();

    public WishlistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        wishlistDao = db.wishlistDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir la liste de souhaits d'un utilisateur
    public LiveData<List<Wishlist>> getUserWishlist(String userId) {
        refreshWishlist(userId);
        return wishlistDao.getWishlistByUser(userId);
    }

    // Méthode pour vérifier si un produit est dans la liste de souhaits
    public LiveData<Boolean> isInWishlist(String userId, String productId) {
        return wishlistDao.isInWishlist(userId, productId);
    }

    // Méthode pour obtenir le nombre d'articles dans la liste de souhaits
    public LiveData<Integer> getWishlistCount(String userId) {
        return wishlistDao.getWishlistCount(userId);
    }

    // Méthode pour ajouter un produit à la liste de souhaits
    public LiveData<ApiResponse<Boolean>> addToWishlist(String userId, String productId) {
        Wishlist wishlistItem = new Wishlist(userId, productId, System.currentTimeMillis());

        // Appel API pour ajouter à la liste de souhaits
        Call<Void> call = apiService.addToWishlist(wishlistItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        wishlistDao.insert(wishlistItem);
                    });
                    wishlistOperationResponse.setValue(new ApiResponse<>(true));
                } else {
                    wishlistOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                wishlistOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de l'ajout à la liste de souhaits: " + t.getMessage());
            }
        });
        return wishlistOperationResponse;
    }

    // Méthode pour supprimer un produit de la liste de souhaits
    public LiveData<ApiResponse<Boolean>> removeFromWishlist(String userId, String productId) {
        // Appel API pour supprimer de la liste de souhaits
        Call<Void> call = apiService.removeFromWishlist(userId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        wishlistDao.deleteByProductId(userId, productId);
                    });
                    wishlistOperationResponse.setValue(new ApiResponse<>(true));
                } else {
                    wishlistOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                wishlistOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la suppression de la liste de souhaits: " + t.getMessage());
            }
        });
        return wishlistOperationResponse;
    }

    // Méthode pour rafraîchir la liste de souhaits depuis le serveur
    private void refreshWishlist(String userId) {
        // Appel API pour récupérer la liste de souhaits
        Call<List<Wishlist>> call = apiService.getUserWishlist(userId);
        call.enqueue(new Callback<List<Wishlist>>() {
            @Override
            public void onResponse(Call<List<Wishlist>> call, Response<List<Wishlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Wishlist> wishlistItems = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (Wishlist item : wishlistItems) {
                            wishlistDao.insert(item);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Wishlist>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération de la liste de souhaits: " + t.getMessage());
            }
        });
    }
}
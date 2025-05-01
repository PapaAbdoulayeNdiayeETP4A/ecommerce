package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.CartDao;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static final String TAG = "CartRepository";
    private CartDao cartDao;
    private ApiService apiService;
    private MutableLiveData<ApiResponse<CartItem>> cartOperationResponse = new MutableLiveData<>();

    public CartRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        cartDao = db.cartDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir les articles du panier d'un utilisateur
    public LiveData<List<CartItem>> getCartItems(String userId) {
        refreshCart(userId);
        return cartDao.getCartItems(userId);
    }

    // Méthode pour obtenir le total du panier
    public LiveData<Double> getCartTotal(String userId) {
        return cartDao.getCartTotal(userId);
    }

    // Méthode pour obtenir le nombre d'articles dans le panier
    public LiveData<Integer> getCartItemCount(String userId) {
        return cartDao.getCartItemCount(userId);
    }

    // Méthode pour ajouter un article au panier
    public LiveData<ApiResponse<CartItem>> addToCart(CartItem cartItem) {
        apiService.addToCart(cartItem).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartItem addedItem = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        cartDao.insert(addedItem);
                    });
                    cartOperationResponse.setValue(new ApiResponse<>(addedItem));
                } else {
                    cartOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                cartOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de l'ajout au panier: " + t.getMessage());
            }
        });
        return cartOperationResponse;
    }

    // Méthode pour mettre à jour un article du panier
    public LiveData<ApiResponse<CartItem>> updateCartItem(String itemId, CartItem cartItem) {
        apiService.updateCartItem(itemId, cartItem).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartItem updatedItem = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        cartDao.update(updatedItem);
                    });
                    cartOperationResponse.setValue(new ApiResponse<>(updatedItem));
                } else {
                    cartOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                cartOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la mise à jour du panier: " + t.getMessage());
            }
        });
        return cartOperationResponse;
    }

    // Méthode pour supprimer un article du panier
    public LiveData<ApiResponse<Boolean>> removeCartItem(String itemId) {
        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();

        apiService.removeCartItem(itemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Supprimer de la base de données locale (implémentation à adapter)
                        // cartDao.deleteById(itemId);
                    });
                    response.setValue(new ApiResponse<>(true));
                } else {
                    response.setValue(new ApiResponse<>("Erreur: " + apiResponse.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                response.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la suppression du panier: " + t.getMessage());
            }
        });
        return response;
    }

    // Méthode pour vider le panier
    public void clearCart(String userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            cartDao.clearCart(userId);
        });
        // Ici, vous pourriez également appeler une API pour vider le panier sur le serveur
    }

    // Méthode pour rafraîchir le panier depuis le serveur
    private void refreshCart(String userId) {
        apiService.getCartItems(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> cartItems = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Vider le panier local et insérer les nouveaux éléments
                        cartDao.clearCart(userId);
                        for (CartItem item : cartItems) {
                            cartDao.insert(item);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération du panier: " + t.getMessage());
            }
        });
    }
}
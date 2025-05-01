package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.ProductDao;
import com.example.ecommerce.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private ProductDao productDao;
    private ApiService apiService;
    private LiveData<List<Product>> allProducts;
    private LiveData<List<Product>> featuredProducts;
    private MutableLiveData<ApiResponse<Product>> productDetailsResponse = new MutableLiveData<>();

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
        apiService = RetrofitClient.getApiService();
        allProducts = productDao.getAllProducts();
        featuredProducts = productDao.getFeaturedProducts();
    }

    // Méthode pour obtenir tous les produits
    public LiveData<List<Product>> getAllProducts() {
        refreshProducts();
        return allProducts;
    }

    // Méthode pour obtenir les produits en vedette
    public LiveData<List<Product>> getFeaturedProducts() {
        refreshFeaturedProducts();
        return featuredProducts;
    }

    // Méthode pour obtenir les produits par catégorie
    public LiveData<List<Product>> getProductsByCategory(String category) {
        refreshProductsByCategory(category);
        return productDao.getProductsByCategory(category);
    }

    // Méthode pour obtenir les détails d'un produit
    public LiveData<ApiResponse<Product>> getProductDetails(String productId) {
        fetchProductDetails(productId);
        return productDetailsResponse;
    }

    // Méthode pour rechercher des produits
    public LiveData<List<Product>> searchProducts(String query) {
        refreshSearchResults(query);
        return productDao.searchProducts(query);
    }

    // Méthode pour rafraîchir la liste de tous les produits
    private void refreshProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Exécuter sur un thread en arrière-plan
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        productDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des produits: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les produits en vedette
    private void refreshFeaturedProducts() {
        apiService.getFeaturedProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        productDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des produits en vedette: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les produits par catégorie
    private void refreshProductsByCategory(String category) {
        apiService.getProductsByCategory(category).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        productDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des produits par catégorie: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les résultats de recherche
    private void refreshSearchResults(String query) {
        apiService.searchProducts(query).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        productDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Échec de la recherche de produits: " + t.getMessage());
            }
        });
    }

    // Méthode pour récupérer les détails d'un produit
    private void fetchProductDetails(String productId) {
        apiService.getProductDetails(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        productDao.insert(product);
                    });
                    productDetailsResponse.setValue(new ApiResponse<>(product));
                } else {
                    productDetailsResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                productDetailsResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la récupération des détails du produit: " + t.getMessage());
            }
        });
    }
}
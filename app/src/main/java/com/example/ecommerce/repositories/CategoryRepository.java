package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.models.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private ApiService apiService;
    private MutableLiveData<List<Category>> allCategories = new MutableLiveData<>();
    private MutableLiveData<List<Category>> popularCategories = new MutableLiveData<>();
    private MutableLiveData<Category> selectedCategory = new MutableLiveData<>();

    public CategoryRepository(Application application) {
        apiService = RetrofitClient.getApiService();
        fetchAllCategories();
        fetchPopularCategories();
    }

    // Méthode pour obtenir toutes les catégories
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    // Méthode pour obtenir les catégories populaires
    public LiveData<List<Category>> getPopularCategories() {
        return popularCategories;
    }

    // Méthode pour obtenir une catégorie par son ID
    public LiveData<Category> getCategoryById(String categoryId) {
        fetchCategoryById(categoryId);
        return selectedCategory;
    }

    // Méthode pour récupérer toutes les catégories depuis le serveur
    private void fetchAllCategories() {
        // Appel API pour récupérer toutes les catégories
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories.setValue(response.body());
                } else {
                    Log.e(TAG, "Erreur lors de la récupération des catégories: " + response.message());
                    // Fournir des données fictives en cas d'échec
                    allCategories.setValue(getDummyCategories());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Échec de la connexion: " + t.getMessage());
                // Fournir des données fictives en cas d'échec
                allCategories.setValue(getDummyCategories());
            }
        });
    }

    // Méthode pour récupérer les catégories populaires depuis le serveur
    private void fetchPopularCategories() {
        // Appel API pour récupérer les catégories populaires
        Call<List<Category>> call = apiService.getPopularCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    popularCategories.setValue(response.body());
                } else {
                    Log.e(TAG, "Erreur lors de la récupération des catégories populaires: " + response.message());
                    // Fournir des données fictives en cas d'échec
                    popularCategories.setValue(getDummyPopularCategories());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Échec de la connexion: " + t.getMessage());
                // Fournir des données fictives en cas d'échec
                popularCategories.setValue(getDummyPopularCategories());
            }
        });
    }

    // Méthode pour récupérer une catégorie par son ID depuis le serveur
    private void fetchCategoryById(String categoryId) {
        // Appel API pour récupérer une catégorie par son ID
        Call<Category> call = apiService.getCategoryById(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedCategory.setValue(response.body());
                } else {
                    Log.e(TAG, "Erreur lors de la récupération de la catégorie: " + response.message());
                    // Fournir une catégorie fictive en cas d'échec
                    selectedCategory.setValue(getDummyCategoryById(categoryId));
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Log.e(TAG, "Échec de la connexion: " + t.getMessage());
                // Fournir une catégorie fictive en cas d'échec
                selectedCategory.setValue(getDummyCategoryById(categoryId));
            }
        });
    }

    // Méthode pour obtenir des catégories fictives (pour le développement/test)
    private List<Category> getDummyCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Électronique", "electronics", 120));
        categories.add(new Category("2", "Vêtements", "clothing", 85));
        categories.add(new Category("3", "Livres", "books", 200));
        categories.add(new Category("4", "Maison", "home", 150));
        categories.add(new Category("5", "Sports", "sports", 90));
        categories.add(new Category("6", "Beauté", "beauty", 75));
        categories.add(new Category("7", "Jouets", "toys", 60));
        categories.add(new Category("8", "Alimentation", "food", 110));
        return categories;
    }

    // Méthode pour obtenir des catégories populaires fictives (pour le développement/test)
    private List<Category> getDummyPopularCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Électronique", "electronics", 120));
        categories.add(new Category("4", "Maison", "home", 150));
        categories.add(new Category("5", "Sports", "sports", 90));
        categories.add(new Category("6", "Beauté", "beauty", 75));
        return categories;
    }

    // Méthode pour obtenir une catégorie fictive par son ID (pour le développement/test)
    private Category getDummyCategoryById(String categoryId) {
        for (Category category : getDummyCategories()) {
            if (category.getCategoryId().equals(categoryId)) {
                return category;
            }
        }
        return new Category(categoryId, "Catégorie " + categoryId, "category_" + categoryId.toLowerCase(), 0);
    }
}
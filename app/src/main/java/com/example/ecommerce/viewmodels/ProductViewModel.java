// viewmodels/ProductViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.repositories.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<String> categoryFilter = new MutableLiveData<>();
    private MutableLiveData<String> selectedProductId = new MutableLiveData<>();

    public ProductViewModel(Application application) {
        super(application);
        repository = new ProductRepository(application);
    }

    // Méthode pour obtenir tous les produits
    public LiveData<List<Product>> getAllProducts() {
        return repository.getAllProducts();
    }

    // Méthode pour obtenir les produits en vedette
    public LiveData<List<Product>> getFeaturedProducts() {
        return repository.getFeaturedProducts();
    }

    // Méthode pour obtenir les produits par catégorie
    public LiveData<List<Product>> getProductsByCategory() {
        return Transformations.switchMap(categoryFilter, category ->
                repository.getProductsByCategory(category)
        );
    }

    // Méthode pour définir la catégorie
    public void setCategory(String category) {
        categoryFilter.setValue(category);
    }

    // Méthode pour obtenir les résultats de recherche
    public LiveData<List<Product>> getSearchResults() {
        return Transformations.switchMap(searchQuery, query ->
                repository.searchProducts(query)
        );
    }

    // Méthode pour définir la requête de recherche
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    // Méthode pour obtenir les détails d'un produit
    public LiveData<ApiResponse<Product>> getProductDetails() {
        return Transformations.switchMap(selectedProductId, productId ->
                repository.getProductDetails(productId)
        );
    }

    // Méthode pour définir le produit sélectionné
    public void setSelectedProductId(String productId) {
        selectedProductId.setValue(productId);
    }
}
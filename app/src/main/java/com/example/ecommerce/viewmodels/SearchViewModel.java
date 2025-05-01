// viewmodels/SearchViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.models.Product;
import com.example.ecommerce.repositories.ProductRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<List<String>> searchFilters = new MutableLiveData<>();
    private MutableLiveData<String> sortOrder = new MutableLiveData<>();

    public SearchViewModel(Application application) {
        super(application);
        repository = new ProductRepository(application);
        sortOrder.setValue("relevance"); // Tri par défaut
    }

    // Définir la requête de recherche
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    // Définir les filtres de recherche
    public void setSearchFilters(List<String> filters) {
        searchFilters.setValue(filters);
    }

    // Définir l'ordre de tri
    public void setSortOrder(String order) {
        sortOrder.setValue(order);
    }

    // Obtenir les résultats de recherche
    public LiveData<List<Product>> getSearchResults() {
        return Transformations.switchMap(searchQuery, query ->
                repository.searchProducts(query)
        );
    }

    // Obtenir les résultats de recherche avancée (avec filtres et tri)
    public LiveData<List<Product>> getAdvancedSearchResults() {
        // Cette méthode serait implémentée pour prendre en compte les filtres et le tri
        // Pour cet exemple, nous utilisons seulement la requête de base
        return getSearchResults();
    }

    // Obtenir l'historique de recherche
    public LiveData<List<String>> getSearchHistory() {
        return repository.getSearchHistory();
    }

    // Ajouter une requête à l'historique
    public void addToSearchHistory(String query) {
        repository.addToSearchHistory(query);
    }

    // Effacer l'historique de recherche
    public void clearSearchHistory() {
        repository.clearSearchHistory();
    }
}
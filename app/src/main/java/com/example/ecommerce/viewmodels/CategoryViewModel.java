// viewmodels/CategoryViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.models.Category;
import com.example.ecommerce.repositories.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository repository;
    private MutableLiveData<String> selectedCategoryId = new MutableLiveData<>();

    public CategoryViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    // Définir la catégorie sélectionnée
    public void setSelectedCategory(String categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    // Obtenir l'ID de la catégorie sélectionnée
    public LiveData<String> getSelectedCategoryId() {
        return selectedCategoryId;
    }

    // Obtenir toutes les catégories
    public LiveData<List<Category>> getAllCategories() {
        return repository.getAllCategories();
    }

    // Obtenir les catégories populaires
    public LiveData<List<Category>> getPopularCategories() {
        return repository.getPopularCategories();
    }

    // Obtenir une catégorie par son ID
    public LiveData<Category> getCategoryById(String categoryId) {
        return repository.getCategoryById(categoryId);
    }
}
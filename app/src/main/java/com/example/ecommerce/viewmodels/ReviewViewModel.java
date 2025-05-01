// viewmodels/ReviewViewModel.java (suite)
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.Review;
import com.example.ecommerce.repositories.ReviewRepository;

import java.util.Date;
import java.util.List;

public class ReviewViewModel extends AndroidViewModel {
    private ReviewRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();
    private MutableLiveData<String> selectedProductId = new MutableLiveData<>();

    public ReviewViewModel(Application application) {
        super(application);
        repository = new ReviewRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Définir le produit sélectionné
    public void setSelectedProduct(String productId) {
        selectedProductId.setValue(productId);
    }

    // Obtenir les avis pour le produit sélectionné
    public LiveData<List<Review>> getProductReviews() {
        return Transformations.switchMap(selectedProductId, productId ->
                repository.getReviewsByProduct(productId)
        );
    }

    // Obtenir les avis de l'utilisateur
    public LiveData<List<Review>> getUserReviews() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getReviewsByUser(userId)
        );
    }

    // Obtenir le nombre d'avis pour le produit sélectionné
    public LiveData<Integer> getReviewCount() {
        return Transformations.switchMap(selectedProductId, productId ->
                repository.getReviewCountForProduct(productId)
        );
    }

    // Obtenir la note moyenne pour le produit sélectionné
    public LiveData<Float> getAverageRating() {
        return Transformations.switchMap(selectedProductId, productId ->
                repository.getAverageRatingForProduct(productId)
        );
    }

    // Ajouter un avis
    public LiveData<ApiResponse<Review>> addReview(String text, float rating, String userName) {
        if (currentUserId.getValue() != null && selectedProductId.getValue() != null) {
            Review review = new Review();
            review.setReviewId(java.util.UUID.randomUUID().toString());
            review.setProductId(selectedProductId.getValue());
            review.setUserId(currentUserId.getValue());
            review.setUserDisplayName(userName);
            review.setText(text);
            review.setRating(rating);
            review.setDate(new Date());
            return repository.addReview(review);
        }

        MutableLiveData<ApiResponse<Review>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Utilisateur ou produit non défini"));
        return response;
    }

    // Mettre à jour un avis
    public LiveData<ApiResponse<Review>> updateReview(Review review) {
        return repository.updateReview(review);
    }

    // Supprimer un avis
    public LiveData<ApiResponse<Boolean>> deleteReview(Review review) {
        return repository.deleteReview(review);
    }
}
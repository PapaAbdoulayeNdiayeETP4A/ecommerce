package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.ReviewDao;
import com.example.ecommerce.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private static final String TAG = "ReviewRepository";
    private ReviewDao reviewDao;
    private ApiService apiService;
    private MutableLiveData<ApiResponse<Review>> reviewOperationResponse = new MutableLiveData<>();

    public ReviewRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        reviewDao = db.reviewDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir les avis d'un produit
    public LiveData<List<Review>> getReviewsByProduct(String productId) {
        refreshReviews(productId);
        return reviewDao.getReviewsByProduct(productId);
    }

    // Méthode pour obtenir les avis d'un utilisateur
    public LiveData<List<Review>> getReviewsByUser(String userId) {
        refreshUserReviews(userId);
        return reviewDao.getReviewsByUser(userId);
    }

    // Méthode pour obtenir le nombre d'avis pour un produit
    public LiveData<Integer> getReviewCountForProduct(String productId) {
        return reviewDao.getReviewCountForProduct(productId);
    }

    // Méthode pour obtenir la note moyenne d'un produit
    public LiveData<Float> getAverageRatingForProduct(String productId) {
        return reviewDao.getAverageRatingForProduct(productId);
    }

    // Méthode pour ajouter un avis
    public LiveData<ApiResponse<Review>> addReview(Review review) {
        // Appel API pour ajouter l'avis
        Call<Review> call = apiService.addReview(review);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Review addedReview = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        reviewDao.insert(addedReview);
                    });
                    reviewOperationResponse.setValue(new ApiResponse<>(addedReview));
                } else {
                    reviewOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                reviewOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de l'ajout d'avis: " + t.getMessage());
            }
        });
        return reviewOperationResponse;
    }

    // Méthode pour mettre à jour un avis
    public LiveData<ApiResponse<Review>> updateReview(Review review) {
        // Appel API pour mettre à jour l'avis
        Call<Review> call = apiService.updateReview(review.getReviewId(), review);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Review updatedReview = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        reviewDao.update(updatedReview);
                    });
                    reviewOperationResponse.setValue(new ApiResponse<>(updatedReview));
                } else {
                    reviewOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                reviewOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la mise à jour d'avis: " + t.getMessage());
            }
        });
        return reviewOperationResponse;
    }

    // Méthode pour supprimer un avis
    public LiveData<ApiResponse<Boolean>> deleteReview(Review review) {
        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();

        // Appel API pour supprimer l'avis
        Call<Void> call = apiService.deleteReview(review.getReviewId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        reviewDao.delete(review);
                    });
                    response.setValue(new ApiResponse<>(true));
                } else {
                    response.setValue(new ApiResponse<>("Erreur: " + apiResponse.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                response.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la suppression d'avis: " + t.getMessage());
            }
        });
        return response;
    }

    // Méthode pour rafraîchir les avis d'un produit
    private void refreshReviews(String productId) {
        // Appel API pour récupérer les avis d'un produit
        Call<List<Review>> call = apiService.getProductReviews(productId);
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (Review review : reviews) {
                            reviewDao.insert(review);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des avis: " + t.getMessage());
            }
        });
    }

    // Méthode pour rafraîchir les avis d'un utilisateur
    private void refreshUserReviews(String userId) {
        // Appel API pour récupérer les avis d'un utilisateur
        Call<List<Review>> call = apiService.getUserReviews(userId);
        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (Review review : reviews) {
                            reviewDao.insert(review);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des avis de l'utilisateur: " + t.getMessage());
            }
        });
    }
}
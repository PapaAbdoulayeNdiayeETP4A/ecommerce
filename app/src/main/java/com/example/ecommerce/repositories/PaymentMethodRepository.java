package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.PaymentMethodDao;
import com.example.ecommerce.models.PaymentMethod;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodRepository {
    private static final String TAG = "PaymentMethodRepository";
    private PaymentMethodDao paymentMethodDao;
    private ApiService apiService;
    private MutableLiveData<ApiResponse<PaymentMethod>> paymentMethodOperationResponse = new MutableLiveData<>();

    public PaymentMethodRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        paymentMethodDao = db.paymentMethodDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir les méthodes de paiement d'un utilisateur
    public LiveData<List<PaymentMethod>> getUserPaymentMethods(String userId) {
        refreshPaymentMethods(userId);
        return paymentMethodDao.getPaymentMethodsByUser(userId);
    }

    // Méthode pour obtenir la méthode de paiement par défaut d'un utilisateur
    public LiveData<PaymentMethod> getDefaultPaymentMethod(String userId) {
        return paymentMethodDao.getDefaultPaymentMethod(userId);
    }

    // Méthode pour ajouter une méthode de paiement
    public LiveData<ApiResponse<PaymentMethod>> addPaymentMethod(PaymentMethod paymentMethod) {
        // Appel API pour ajouter la méthode de paiement
        Call<PaymentMethod> call = apiService.addPaymentMethod(paymentMethod);
        call.enqueue(new Callback<PaymentMethod>() {
            @Override
            public void onResponse(Call<PaymentMethod> call, Response<PaymentMethod> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentMethod addedPaymentMethod = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        paymentMethodDao.insert(addedPaymentMethod);
                    });
                    paymentMethodOperationResponse.setValue(new ApiResponse<>(addedPaymentMethod));
                } else {
                    paymentMethodOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<PaymentMethod> call, Throwable t) {
                paymentMethodOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de l'ajout de méthode de paiement: " + t.getMessage());
            }
        });
        return paymentMethodOperationResponse;
    }

    // Méthode pour mettre à jour une méthode de paiement
    public LiveData<ApiResponse<PaymentMethod>> updatePaymentMethod(PaymentMethod paymentMethod) {
        // Appel API pour mettre à jour la méthode de paiement
        Call<PaymentMethod> call = apiService.updatePaymentMethod(String.valueOf(paymentMethod.getId()), paymentMethod);
        call.enqueue(new Callback<PaymentMethod>() {
            @Override
            public void onResponse(Call<PaymentMethod> call, Response<PaymentMethod> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentMethod updatedPaymentMethod = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        paymentMethodDao.update(updatedPaymentMethod);
                    });
                    paymentMethodOperationResponse.setValue(new ApiResponse<>(updatedPaymentMethod));
                } else {
                    paymentMethodOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<PaymentMethod> call, Throwable t) {
                paymentMethodOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la mise à jour de méthode de paiement: " + t.getMessage());
            }
        });
        return paymentMethodOperationResponse;
    }

    // Méthode pour supprimer une méthode de paiement
    public LiveData<ApiResponse<Boolean>> deletePaymentMethod(PaymentMethod paymentMethod) {
        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();

        // Appel API pour supprimer la méthode de paiement
        Call<Void> call = apiService.deletePaymentMethod(String.valueOf(paymentMethod.getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        paymentMethodDao.delete(paymentMethod);
                    });
                    response.setValue(new ApiResponse<>(true));
                } else {
                    response.setValue(new ApiResponse<>("Erreur: " + apiResponse.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                response.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la suppression de méthode de paiement: " + t.getMessage());
            }
        });
        return response;
    }

    // Méthode pour définir une méthode de paiement comme méthode par défaut
    public void setDefaultPaymentMethod(String userId, long paymentMethodId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentMethodDao.resetDefaultPaymentMethods(userId);
            paymentMethodDao.setDefaultPaymentMethod(paymentMethodId);
        });

        // Appel API pour définir la méthode de paiement par défaut
        // Implémentation à adapter selon votre API
    }

    // Méthode pour rafraîchir les méthodes de paiement depuis le serveur
    private void refreshPaymentMethods(String userId) {
        // Appel API pour récupérer les méthodes de paiement
        Call<List<PaymentMethod>> call = apiService.getUserPaymentMethods(userId);
        call.enqueue(new Callback<List<PaymentMethod>>() {
            @Override
            public void onResponse(Call<List<PaymentMethod>> call, Response<List<PaymentMethod>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PaymentMethod> paymentMethods = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (PaymentMethod paymentMethod : paymentMethods) {
                            paymentMethodDao.insert(paymentMethod);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<PaymentMethod>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des méthodes de paiement: " + t.getMessage());
            }
        });
    }
}
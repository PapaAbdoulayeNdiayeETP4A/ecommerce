// viewmodels/PaymentMethodViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.PaymentMethod;
import com.example.ecommerce.repositories.PaymentMethodRepository;

import java.util.List;

public class PaymentMethodViewModel extends AndroidViewModel {
    private PaymentMethodRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public PaymentMethodViewModel(Application application) {
        super(application);
        repository = new PaymentMethodRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir les méthodes de paiement de l'utilisateur
    public LiveData<List<PaymentMethod>> getUserPaymentMethods() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserPaymentMethods(userId)
        );
    }

    // Obtenir la méthode de paiement par défaut
    public LiveData<PaymentMethod> getDefaultPaymentMethod() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getDefaultPaymentMethod(userId)
        );
    }

    // Ajouter une méthode de paiement
    public LiveData<ApiResponse<PaymentMethod>> addPaymentMethod(PaymentMethod paymentMethod) {
        if (currentUserId.getValue() != null) {
            paymentMethod.setUserId(currentUserId.getValue());
        }
        return repository.addPaymentMethod(paymentMethod);
    }

    // Mettre à jour une méthode de paiement
    public LiveData<ApiResponse<PaymentMethod>> updatePaymentMethod(PaymentMethod paymentMethod) {
        return repository.updatePaymentMethod(paymentMethod);
    }

    // Supprimer une méthode de paiement
    public LiveData<ApiResponse<Boolean>> deletePaymentMethod(PaymentMethod paymentMethod) {
        return repository.deletePaymentMethod(paymentMethod);
    }

    // Définir une méthode de paiement comme méthode par défaut
    public void setDefaultPaymentMethod(long paymentMethodId) {
        if (currentUserId.getValue() != null) {
            repository.setDefaultPaymentMethod(currentUserId.getValue(), paymentMethodId);
        }
    }
}
// viewmodels/AddressViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.Address;
import com.example.ecommerce.repositories.AddressRepository;

import java.util.List;

public class AddressViewModel extends AndroidViewModel {
    private AddressRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public AddressViewModel(Application application) {
        super(application);
        repository = new AddressRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir les adresses de l'utilisateur
    public LiveData<List<Address>> getUserAddresses() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserAddresses(userId)
        );
    }

    // Obtenir l'adresse par défaut
    public LiveData<Address> getDefaultAddress() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getDefaultAddress(userId)
        );
    }

    // Ajouter une adresse
    public LiveData<ApiResponse<Address>> addAddress(Address address) {
        if (currentUserId.getValue() != null) {
            address.setUserId(currentUserId.getValue());
        }
        return repository.addAddress(address);
    }

    // Mettre à jour une adresse
    public LiveData<ApiResponse<Address>> updateAddress(Address address) {
        return repository.updateAddress(address);
    }

    // Supprimer une adresse
    public LiveData<ApiResponse<Boolean>> deleteAddress(Address address) {
        return repository.deleteAddress(address);
    }

    // Définir une adresse comme adresse par défaut
    public void setDefaultAddress(long addressId) {
        if (currentUserId.getValue() != null) {
            repository.setDefaultAddress(currentUserId.getValue(), addressId);
        }
    }
}
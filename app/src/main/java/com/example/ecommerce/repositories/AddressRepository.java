package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AddressDao;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.models.Address;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private static final String TAG = "AddressRepository";
    private AddressDao addressDao;
    private ApiService apiService;
    private LiveData<List<Address>> userAddresses;
    private MutableLiveData<ApiResponse<Address>> addressOperationResponse = new MutableLiveData<>();

    public AddressRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        addressDao = db.addressDao();
        apiService = RetrofitClient.getApiService();
    }

    // Méthode pour obtenir les adresses d'un utilisateur
    public LiveData<List<Address>> getUserAddresses(String userId) {
        userAddresses = addressDao.getAddressesByUser(userId);
        refreshAddresses(userId);
        return userAddresses;
    }

    // Méthode pour obtenir l'adresse par défaut d'un utilisateur
    public LiveData<Address> getDefaultAddress(String userId) {
        return addressDao.getDefaultAddress(userId);
    }

    // Méthode pour ajouter une adresse
    public LiveData<ApiResponse<Address>> addAddress(Address address) {
        // Appel API pour ajouter l'adresse
        Call<Address> call = apiService.addAddress(address);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Address addedAddress = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        addressDao.insert(addedAddress);
                    });
                    addressOperationResponse.setValue(new ApiResponse<>(addedAddress));
                } else {
                    addressOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                addressOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de l'ajout d'adresse: " + t.getMessage());
            }
        });
        return addressOperationResponse;
    }

    // Méthode pour mettre à jour une adresse
    public LiveData<ApiResponse<Address>> updateAddress(Address address) {
        // Appel API pour mettre à jour l'adresse
        Call<Address> call = apiService.updateAddress(String.valueOf(address.getId()), address);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Address updatedAddress = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        addressDao.update(updatedAddress);
                    });
                    addressOperationResponse.setValue(new ApiResponse<>(updatedAddress));
                } else {
                    addressOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                addressOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la mise à jour d'adresse: " + t.getMessage());
            }
        });
        return addressOperationResponse;
    }

    // Méthode pour supprimer une adresse
    public LiveData<ApiResponse<Boolean>> deleteAddress(Address address) {
        MutableLiveData<ApiResponse<Boolean>> response = new MutableLiveData<>();

        // Appel API pour supprimer l'adresse
        Call<Void> call = apiService.deleteAddress(String.valueOf(address.getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        addressDao.delete(address);
                    });
                    response.setValue(new ApiResponse<>(true));
                } else {
                    response.setValue(new ApiResponse<>("Erreur: " + apiResponse.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                response.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la suppression d'adresse: " + t.getMessage());
            }
        });
        return response;
    }

    // Méthode pour définir une adresse comme adresse par défaut
    public void setDefaultAddress(String userId, long addressId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            addressDao.resetDefaultAddresses(userId);
            addressDao.setDefaultAddress(addressId);
        });

        // Appel API pour définir l'adresse par défaut
        // Implémentation à adapter selon votre API
    }

    // Méthode pour rafraîchir les adresses depuis le serveur
    private void refreshAddresses(String userId) {
        // Appel API pour récupérer les adresses
        Call<List<Address>> call = apiService.getUserAddresses(userId);
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addresses = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        // Mettre à jour la base de données locale
                        for (Address address : addresses) {
                            addressDao.insert(address);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération des adresses: " + t.getMessage());
            }
        });
    }
}
// viewmodels/UserViewModel.java
package com.example.ecommerce.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.models.User;
import com.example.ecommerce.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    // Définir l'utilisateur actuel
    public void setCurrentUser(String userId) {
        currentUserId.setValue(userId);
    }

    // Obtenir l'utilisateur Firebase actuel
    public LiveData<FirebaseUser> getCurrentFirebaseUser() {
        return repository.getCurrentFirebaseUser();
    }

    // Enregistrer un utilisateur
    public LiveData<ApiResponse<User>> registerUser(User user, String password) {
        return repository.registerUser(user, password);
    }

    // Connexion utilisateur
    public LiveData<ApiResponse<User>> loginUser(String email, String password) {
        return repository.loginUser(email, password);
    }

    // Déconnexion
    public void logout() {
        repository.logout();
    }

    // Obtenir le profil utilisateur
    public LiveData<User> getUserProfile() {
        return Transformations.switchMap(currentUserId, userId ->
                repository.getUserProfile(userId)
        );
    }

    // Mettre à jour le profil utilisateur
    public LiveData<ApiResponse<User>> updateUserProfile(User user) {
        if (currentUserId.getValue() != null) {
            return repository.updateUserProfile(currentUserId.getValue(), user);
        }
        MutableLiveData<ApiResponse<User>> response = new MutableLiveData<>();
        response.setValue(new ApiResponse<>("Aucun utilisateur connecté"));
        return response;
    }
}
package com.example.ecommerce.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.api.ApiResponse;
import com.example.ecommerce.api.ApiService;
import com.example.ecommerce.api.RetrofitClient;
import com.example.ecommerce.database.AppDatabase;
import com.example.ecommerce.database.UserDao;
import com.example.ecommerce.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private UserDao userDao;
    private ApiService apiService;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<ApiResponse<User>> userOperationResponse = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> firebaseUserLiveData = new MutableLiveData<>();

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        apiService = RetrofitClient.getApiService();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserLiveData.setValue(firebaseAuth.getCurrentUser());

        // Écouter les changements d'authentification Firebase
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            firebaseUserLiveData.setValue(firebaseAuth.getCurrentUser());
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // Méthode pour l'enregistrement d'un utilisateur
    public LiveData<ApiResponse<User>> registerUser(final User user, String password) {
        // D'abord, créer l'utilisateur dans Firebase
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        // Obtenir l'ID Firebase
                        String firebaseUserId = task.getResult().getUser().getUid();
                        user.setUserId(firebaseUserId);

                        // Ensuite, enregistrer l'utilisateur dans notre API
                        apiService.registerUser(user).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    User registeredUser = response.body();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        userDao.insert(registeredUser);
                                    });
                                    userOperationResponse.setValue(new ApiResponse<>(registeredUser));
                                } else {
                                    // Si l'API échoue, supprimer l'utilisateur Firebase
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        firebaseAuth.getCurrentUser().delete();
                                    }
                                    userOperationResponse.setValue(new ApiResponse<>("Erreur API: " + response.message()));
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                // Si l'API est injoignable, supprimer l'utilisateur Firebase
                                if (firebaseAuth.getCurrentUser() != null) {
                                    firebaseAuth.getCurrentUser().delete();
                                }
                                userOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                                Log.e(TAG, "Échec de l'enregistrement utilisateur: " + t.getMessage());
                            }
                        });
                    } else {
                        userOperationResponse.setValue(new ApiResponse<>("Erreur Firebase: " +
                                (task.getException() != null ? task.getException().getMessage() : "Inconnu")));
                    }
                });

        return userOperationResponse;
    }

    // Méthode pour la connexion d'un utilisateur
    public LiveData<ApiResponse<User>> loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        String firebaseUserId = task.getResult().getUser().getUid();
                        // Récupérer les données utilisateur depuis l'API
                        apiService.getUserProfile(firebaseUserId).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    User loggedInUser = response.body();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        userDao.insert(loggedInUser);
                                    });
                                    userOperationResponse.setValue(new ApiResponse<>(loggedInUser));
                                } else {
                                    userOperationResponse.setValue(new ApiResponse<>("Erreur API: " + response.message()));
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                userOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                                Log.e(TAG, "Échec de la récupération du profil: " + t.getMessage());
                            }
                        });
                    } else {
                        userOperationResponse.setValue(new ApiResponse<>("Erreur d'authentification: " +
                                (task.getException() != null ? task.getException().getMessage() : "Inconnu")));
                    }
                });

        return userOperationResponse;
    }

    // Méthode pour la déconnexion
    public void logout() {
        firebaseAuth.signOut();
    }

    // Méthode pour obtenir l'utilisateur actuel de Firebase
    public LiveData<FirebaseUser> getCurrentFirebaseUser() {
        return firebaseUserLiveData;
    }

    // Méthode pour obtenir le profil utilisateur
    public LiveData<User> getUserProfile(String userId) {
        refreshUserProfile(userId);
        return userDao.getUserById(userId);
    }

    // Méthode pour mettre à jour le profil utilisateur
    public LiveData<ApiResponse<User>> updateUserProfile(String userId, User user) {
        apiService.updateUserProfile(userId, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        userDao.update(updatedUser);
                    });
                    userOperationResponse.setValue(new ApiResponse<>(updatedUser));
                } else {
                    userOperationResponse.setValue(new ApiResponse<>("Erreur: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userOperationResponse.setValue(new ApiResponse<>("Échec de la connexion: " + t.getMessage()));
                Log.e(TAG, "Échec de la mise à jour du profil: " + t.getMessage());
            }
        });
        return userOperationResponse;
    }

    // Méthode pour rafraîchir le profil utilisateur
    private void refreshUserProfile(String userId) {
        apiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        userDao.insert(user);
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Échec de la récupération du profil utilisateur: " + t.getMessage());
            }
        });
    }
}
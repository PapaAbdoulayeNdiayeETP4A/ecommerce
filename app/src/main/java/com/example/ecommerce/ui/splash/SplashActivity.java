// ui/splash/SplashActivity.java
package com.example.ecommerce.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.ui.auth.LoginActivity;
import com.example.ecommerce.ui.home.MainActivity;
import com.example.ecommerce.viewmodels.UserViewModel;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500; // 1.5 secondes
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Vérifie si l'utilisateur est déjà connecté
        userViewModel.getCurrentFirebaseUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Utilisateur connecté, rediriger vers l'écran principal après le délai
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, SPLASH_DELAY);
            } else {
                // Utilisateur non connecté, rediriger vers l'écran de connexion après le délai
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }, SPLASH_DELAY);
            }
        });
    }
}
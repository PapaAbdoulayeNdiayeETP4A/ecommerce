// ui/auth/LoginActivity.java
package com.example.ecommerce.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityLoginBinding;
import com.example.ecommerce.ui.home.MainActivity;
import com.example.ecommerce.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser le ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Vérifier si l'utilisateur est déjà connecté
        userViewModel.getCurrentFirebaseUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                navigateToMainActivity();
            }
        });

        // Configurer les écouteurs de clics
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Bouton de connexion
        binding.btnLogin.setOnClickListener(v -> loginUser());

        // Lien d'inscription
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Lien "Mot de passe oublié"
        binding.tvForgotPassword.setOnClickListener(v -> {
            // Implémenter la logique de réinitialisation du mot de passe ici
            Toast.makeText(LoginActivity.this, "Fonctionnalité à implémenter", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validation des champs
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.email_required));
            return;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.password_required));
            return;
        }

        // Afficher la barre de progression
        binding.progressBar.setVisibility(View.VISIBLE);

        // Tentative de connexion
        userViewModel.loginUser(email, password).observe(this, response -> {
            // Cacher la barre de progression
            binding.progressBar.setVisibility(View.GONE);

            if (response.isSuccess()) {
                // Connexion réussie
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                // Échec de la connexion
                Toast.makeText(LoginActivity.this, response.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
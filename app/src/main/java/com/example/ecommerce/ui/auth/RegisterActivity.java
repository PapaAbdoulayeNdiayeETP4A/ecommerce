// ui/auth/RegisterActivity.java
package com.example.ecommerce.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityRegisterBinding;
import com.example.ecommerce.models.User;
import com.example.ecommerce.ui.home.MainActivity;
import com.example.ecommerce.viewmodels.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser le ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Configurer les écouteurs de clics
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Bouton d'inscription
        binding.btnRegister.setOnClickListener(v -> registerUser());

        // Lien de connexion
        binding.tvLogin.setOnClickListener(v -> {
            finish(); // Retourner à l'écran de connexion
        });
    }

    private void registerUser() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Validation des champs
        if (name.isEmpty()) {
            binding.tilName.setError(getString(R.string.name_required));
            return;
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.email_required));
            return;
        }

        if (phone.isEmpty()) {
            binding.tilPhone.setError(getString(R.string.phone_required));
            return;
        }

        if (address.isEmpty()) {
            binding.tilAddress.setError(getString(R.string.address_required));
            return;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.password_required));
            return;
        }

        if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.password_length));
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.password_mismatch));
            return;
        }

        // Créer l'objet utilisateur
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        // Afficher la barre de progression
        binding.progressBar.setVisibility(View.VISIBLE);

        // Tentative d'inscription
        userViewModel.registerUser(user, password).observe(this, response -> {
            // Cacher la barre de progression
            binding.progressBar.setVisibility(View.GONE);

            if (response.isSuccess()) {
                // Inscription réussie
                Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                // Échec de l'inscription
                Toast.makeText(RegisterActivity.this, response.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
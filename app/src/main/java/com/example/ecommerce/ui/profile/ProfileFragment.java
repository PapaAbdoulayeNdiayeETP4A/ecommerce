// ui/profile/ProfileFragment.java
package com.example.ecommerce.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentProfileBinding;
import com.example.ecommerce.models.User;
import com.example.ecommerce.ui.auth.LoginActivity;
import com.example.ecommerce.ui.orders.OrdersFragment;
import com.example.ecommerce.ui.wishlist.WishlistFragment;
import com.example.ecommerce.viewmodels.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Observer l'utilisateur actuel
        userViewModel.getCurrentFirebaseUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                // Utilisateur non connecté, rediriger vers l'écran de connexion
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Observer le profil utilisateur
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUserProfile);

        // Configurer les écouteurs de clics
        setupClickListeners();
    }

    private void updateUserProfile(User user) {
        if (user != null) {
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getEmail());

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .circleCrop()
                        .into(binding.ivUserProfile);
            }
        }
    }

    private void setupClickListeners() {
        // Clic sur Modifier le profil
        binding.btnEditProfile.setOnClickListener(v -> {
            navigateToEditProfile();
        });

        // Clic sur Mes commandes
        binding.cardOrders.setOnClickListener(v -> {
            navigateToOrders();
        });

        // Clic sur Ma liste de souhaits
        binding.cardWishlist.setOnClickListener(v -> {
            navigateToWishlist();
        });

        // Clic sur Mes adresses
        binding.cardAddresses.setOnClickListener(v -> {
            navigateToAddresses();
        });

        // Clic sur Mes méthodes de paiement
        binding.cardPaymentMethods.setOnClickListener(v -> {
            navigateToPaymentMethods();
        });

        // Clic sur Paramètres
        binding.cardSettings.setOnClickListener(v -> {
            navigateToSettings();
        });

        // Clic sur Déconnexion
        binding.btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void navigateToEditProfile() {
        // Implémenter la navigation vers l'écran de modification du profil
        Toast.makeText(requireContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
    }

    private void navigateToOrders() {
        // Naviguer vers l'écran des commandes
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new OrdersFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToWishlist() {
        // Naviguer vers l'écran de la liste de souhaits
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new WishlistFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToAddresses() {
        // Implémenter la navigation vers l'écran des adresses
        Toast.makeText(requireContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
    }

    private void navigateToPaymentMethods() {
        // Implémenter la navigation vers l'écran des méthodes de paiement
        Toast.makeText(requireContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSettings() {
        // Implémenter la navigation vers l'écran des paramètres
        Toast.makeText(requireContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    logout();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void logout() {
        userViewModel.logout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
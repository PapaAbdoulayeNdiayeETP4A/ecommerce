// ui/product/ProductReviewsFragment.java (suite)
package com.example.ecommerce.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.ReviewAdapter;
import com.example.ecommerce.databinding.FragmentProductReviewsBinding;
import com.example.ecommerce.models.Review;
import com.example.ecommerce.viewmodels.ReviewViewModel;
import com.example.ecommerce.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductReviewsFragment extends Fragment {

    private FragmentProductReviewsBinding binding;
    private ReviewViewModel reviewViewModel;
    private UserViewModel userViewModel;
    private ReviewAdapter reviewAdapter;
    private String productId;
    private String currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductReviewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer l'ID du produit à partir des arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("productId")) {
            productId = args.getString("productId");
        } else {
            // ID de produit non trouvé, revenir en arrière
            requireActivity().onBackPressed();
            return;
        }

        // Initialiser les ViewModels
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Configurer l'adaptateur
        reviewAdapter = new ReviewAdapter(requireContext(), new ArrayList<>());
        binding.rvReviews.setAdapter(reviewAdapter);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Configurer la toolbar
        binding.toolbar.setTitle(R.string.reviews);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Observer l'utilisateur actuel
        userViewModel.getCurrentFirebaseUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                currentUserId = firebaseUser.getUid();
                userViewModel.setCurrentUser(currentUserId);
                // Afficher le formulaire d'avis uniquement si l'utilisateur est connecté
                binding.layoutAddReview.setVisibility(View.VISIBLE);
            } else {
                binding.layoutAddReview.setVisibility(View.GONE);
            }
        });

        // Configurer le bouton d'envoi d'avis
        binding.btnSubmitReview.setOnClickListener(v -> submitReview());

        // Charger les avis
        loadReviews();
    }

    private void loadReviews() {
        binding.progressBar.setVisibility(View.VISIBLE);
        reviewViewModel.setSelectedProduct(productId);
        reviewViewModel.getProductReviews().observe(getViewLifecycleOwner(), this::updateReviews);
    }

    private void updateReviews(List<Review> reviews) {
        binding.progressBar.setVisibility(View.GONE);

        if (reviews != null && !reviews.isEmpty()) {
            reviewAdapter.updateReviews(reviews);
            binding.rvReviews.setVisibility(View.VISIBLE);
            binding.tvNoReviews.setVisibility(View.GONE);

            // Mettre à jour les statistiques d'avis
            updateReviewStats(reviews);
        } else {
            binding.rvReviews.setVisibility(View.GONE);
            binding.tvNoReviews.setVisibility(View.VISIBLE);
        }
    }

    private void updateReviewStats(List<Review> reviews) {
        // Calculer le nombre d'avis par note (5 étoiles, 4 étoiles, etc.)
        int[] ratingCounts = new int[5];
        float totalRating = 0;

        for (Review review : reviews) {
            int rating = (int) review.getRating() - 1; // L'indice 0 représente 1 étoile
            if (rating >= 0 && rating < 5) {
                ratingCounts[rating]++;
                totalRating += review.getRating();
            }
        }

        // Calculer la note moyenne
        float averageRating = totalRating / reviews.size();
        binding.ratingBar.setRating(averageRating);
        binding.tvAverageRating.setText(String.format("%.1f", averageRating));
        binding.tvReviewCount.setText(getString(R.string.review_count, reviews.size()));

        // Mettre à jour les barres de progression
        int totalReviews = reviews.size();
        binding.progressBar5.setProgress((int) ((float) ratingCounts[4] / totalReviews * 100));
        binding.progressBar4.setProgress((int) ((float) ratingCounts[3] / totalReviews * 100));
        binding.progressBar3.setProgress((int) ((float) ratingCounts[2] / totalReviews * 100));
        binding.progressBar2.setProgress((int) ((float) ratingCounts[1] / totalReviews * 100));
        binding.progressBar1.setProgress((int) ((float) ratingCounts[0] / totalReviews * 100));

        // Afficher le nombre d'avis par note
        binding.tvCount5.setText(String.valueOf(ratingCounts[4]));
        binding.tvCount4.setText(String.valueOf(ratingCounts[3]));
        binding.tvCount3.setText(String.valueOf(ratingCounts[2]));
        binding.tvCount2.setText(String.valueOf(ratingCounts[1]));
        binding.tvCount1.setText(String.valueOf(ratingCounts[0]));
    }

    private void submitReview() {
        // Récupérer les données du formulaire
        String reviewText = binding.etReviewText.getText().toString().trim();
        float rating = binding.ratingBarInput.getRating();

        if (reviewText.isEmpty()) {
            binding.etReviewText.setError(getString(R.string.review_text_required));
            return;
        }

        if (rating == 0) {
            Toast.makeText(requireContext(), R.string.rating_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier que l'utilisateur est connecté
        if (currentUserId == null) {
            Toast.makeText(requireContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtenir le nom de l'utilisateur
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String userName = user.getName();

                // Désactiver le bouton pour éviter les soumissions multiples
                binding.btnSubmitReview.setEnabled(false);
                binding.progressBarSubmit.setVisibility(View.VISIBLE);

                // Soumettre l'avis
                reviewViewModel.addReview(reviewText, rating, userName).observe(getViewLifecycleOwner(), response -> {
                    binding.progressBarSubmit.setVisibility(View.GONE);
                    binding.btnSubmitReview.setEnabled(true);

                    if (response.isSuccess()) {
                        // Réinitialiser le formulaire
                        binding.etReviewText.setText("");
                        binding.ratingBarInput.setRating(0);

                        Toast.makeText(requireContext(), R.string.review_submitted, Toast.LENGTH_SHORT).show();

                        // Recharger les avis
                        loadReviews();
                    } else {
                        Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
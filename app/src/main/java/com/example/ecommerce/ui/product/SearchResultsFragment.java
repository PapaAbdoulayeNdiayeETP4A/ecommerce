// ui/product/SearchResultsFragment.java
package com.example.ecommerce.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.databinding.FragmentSearchResultsBinding;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FragmentSearchResultsBinding binding;
    private SearchViewModel searchViewModel;
    private ProductAdapter productAdapter;
    private String searchQuery;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer la requête de recherche des arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("query")) {
            searchQuery = args.getString("query");
        } else {
            // Aucune requête, revenir en arrière
            requireActivity().onBackPressed();
            return;
        }

        // Initialiser le ViewModel
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        // Configurer l'adaptateur
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), this, ProductAdapter.VIEW_TYPE_GRID);
        binding.rvSearchResults.setAdapter(productAdapter);
        binding.rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Configurer la toolbar
        binding.toolbar.setTitle(getString(R.string.search_results_for, searchQuery));
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Effectuer la recherche
        performSearch();

        // Configurer les filtres et le tri
        setupFiltersAndSort();
    }

    private void performSearch() {
        binding.progressBar.setVisibility(View.VISIBLE);
        searchViewModel.setSearchQuery(searchQuery);
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), this::updateSearchResults);

        // Ajouter la requête à l'historique
        searchViewModel.addToSearchHistory(searchQuery);
    }

    private void updateSearchResults(List<Product> products) {
        binding.progressBar.setVisibility(View.GONE);

        if (products != null && !products.isEmpty()) {
            productAdapter.updateProducts(products);
            binding.rvSearchResults.setVisibility(View.VISIBLE);
            binding.tvNoResults.setVisibility(View.GONE);
        } else {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.tvNoResults.setVisibility(View.VISIBLE);
            binding.tvNoResults.setText(getString(R.string.no_results_for, searchQuery));
        }
    }

    private void setupFiltersAndSort() {
        // Configurer les filtres
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());

        // Configurer le tri
        binding.btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void showFilterDialog() {
        // Afficher une boîte de dialogue pour les filtres (catégorie, prix, etc.)
        // À implémenter selon les besoins spécifiques
    }

    private void showSortDialog() {
        // Afficher une boîte de dialogue pour les options de tri (prix, popularité, etc.)
        // À implémenter selon les besoins spécifiques
    }

    @Override
    public void onProductClick(Product product) {
        // Naviguer vers les détails du produit
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("productId", product.getProductId());
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
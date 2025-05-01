// ui/product/ProductListFragment.java
package com.example.ecommerce.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.databinding.FragmentProductListBinding;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.viewmodels.ProductViewModel;

import java.util.ArrayList;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FragmentProductListBinding binding;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private String categoryId;
    private String filter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer les arguments (categoryId ou filter)
        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getString("categoryId");
            filter = args.getString("filter");
        }

        // Initialiser le ViewModel
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Configurer le titre de la toolbar
        setupToolbar();

        // Configurer l'adaptateur
        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), this, ProductAdapter.VIEW_TYPE_GRID);
        binding.rvProducts.setAdapter(productAdapter);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Observer les produits en fonction des filtres
        observeProducts();

        // Configurer la recherche
        setupSearch();
    }

    private void setupToolbar() {
        if (categoryId != null) {
            // Charger le nom de la catégorie
            productViewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
                if (category != null) {
                    binding.toolbar.setTitle(category.getName());
                } else {
                    binding.toolbar.setTitle(R.string.products);
                }
            });
        } else if ("featured".equals(filter)) {
            binding.toolbar.setTitle(R.string.featured_products);
        } else if ("new".equals(filter)) {
            binding.toolbar.setTitle(R.string.new_arrivals);
        } else {
            binding.toolbar.setTitle(R.string.products);
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void observeProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (categoryId != null) {
            // Charger les produits par catégorie
            productViewModel.setCategory(categoryId);
            productViewModel.getProductsByCategory().observe(getViewLifecycleOwner(), this::updateProductList);
        } else if ("featured".equals(filter)) {
            // Charger les produits en vedette
            productViewModel.getFeaturedProducts().observe(getViewLifecycleOwner(), this::updateProductList);
        } else {
            // Charger tous les produits
            productViewModel.getAllProducts().observe(getViewLifecycleOwner(), this::updateProductList);
        }
    }

    private void updateProductList(List<Product> products) {
        binding.progressBar.setVisibility(View.GONE);

        if (products != null && !products.isEmpty()) {
            productAdapter.updateProducts(products);
            binding.rvProducts.setVisibility(View.VISIBLE);
            binding.tvEmptyView.setVisibility(View.GONE);
        } else {
            binding.rvProducts.setVisibility(View.GONE);
            binding.tvEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchProducts(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Option: Rechercher pendant la frappe pour une expérience plus dynamique
                // Si l'on préfère attendre la soumission, ne rien faire ici
                return false;
            }
        });
    }

    private void searchProducts(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        productViewModel.setSearchQuery(query);
        productViewModel.getSearchResults().observe(getViewLifecycleOwner(), products -> {
            binding.progressBar.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                productAdapter.updateProducts(products);
                binding.rvProducts.setVisibility(View.VISIBLE);
                binding.tvEmptyView.setVisibility(View.GONE);
            } else {
                binding.rvProducts.setVisibility(View.GONE);
                binding.tvEmptyView.setVisibility(View.VISIBLE);
            }
        });
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
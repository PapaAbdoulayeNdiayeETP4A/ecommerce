// ui/home/HomeFragment.java
package com.example.ecommerce.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.adapters.CategoryAdapter;
import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.databinding.FragmentHomeBinding;
import com.example.ecommerce.models.Category;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.ui.product.ProductDetailFragment;
import com.example.ecommerce.ui.product.ProductListFragment;
import com.example.ecommerce.viewmodels.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener, CategoryAdapter.OnCategoryClickListener {

    private FragmentHomeBinding binding;
    private ProductViewModel productViewModel;
    private ProductAdapter featuredProductsAdapter;
    private ProductAdapter newProductsAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Configurer le SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // Configurer les adaptateurs
        setupCategoriesRecyclerView();
        setupFeaturedProductsRecyclerView();
        setupNewProductsRecyclerView();

        // Configurer les bannières (à implémenter)
        setupBanners();

        // Observer les données
        observeViewModel();

        // Configurer les écouteurs de clics
        setupClickListeners();
    }

    private void setupCategoriesRecyclerView() {
        categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<>(), this);
        binding.rvCategories.setAdapter(categoryAdapter);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupFeaturedProductsRecyclerView() {
        featuredProductsAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), this, ProductAdapter.VIEW_TYPE_HORIZONTAL);
        binding.rvFeaturedProducts.setAdapter(featuredProductsAdapter);
        binding.rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupNewProductsRecyclerView() {
        newProductsAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), this, ProductAdapter.VIEW_TYPE_GRID);
        binding.rvNewProducts.setAdapter(newProductsAdapter);
        binding.rvNewProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    private void setupBanners() {
        // Implémenter la configuration des bannières
        // Cela peut impliquer l'utilisation d'un ViewPager avec un adapter personnalisé
    }

    private void observeViewModel() {
        // Observer les produits en vedette
        productViewModel.getFeaturedProducts().observe(getViewLifecycleOwner(), products -> {
            featuredProductsAdapter.updateProducts(products);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        // Observer tous les produits (pour les nouveaux arrivages)
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            // Pour simplifier, nous utilisons tous les produits comme nouveaux arrivages
            // Dans une application réelle, vous pourriez avoir une API distincte pour les nouveaux produits
            newProductsAdapter.updateProducts(products);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        // Dans une application réelle, vous auriez également une observation pour les catégories
        // Ici, nous utilisons des données fictives pour l'exemple
        List<Category> dummyCategories = getDummyCategories();
        categoryAdapter.updateCategories(dummyCategories);
    }

    private List<Category> getDummyCategories() {
        // Créer des catégories fictives pour l'exemple
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Electronics", "electronics", 120));
        categories.add(new Category("2", "Clothing", "clothing", 85));
        categories.add(new Category("3", "Books", "books", 200));
        categories.add(new Category("4", "Home", "home", 150));
        categories.add(new Category("5", "Sports", "sports", 90));
        return categories;
    }

    private void setupClickListeners() {
        // Voir tous les produits en vedette
        binding.tvSeeAllFeatured.setOnClickListener(v -> {
            // Naviguer vers la liste de produits filtré par "featured"
            navigateToProductList("featured");
        });

        // Voir tous les nouveaux arrivages
        binding.tvSeeAllNewArrivals.setOnClickListener(v -> {
            // Naviguer vers la liste de produits
            navigateToProductList(null);
        });
    }

    private void refreshData() {
        binding.swipeRefreshLayout.setRefreshing(true);
        // Dans une application réelle, vous appelleriez des méthodes pour rafraîchir les données
        // Pour cet exemple, nous simulons simplement un délai
        binding.swipeRefreshLayout.postDelayed(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(requireContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }, 1000);
    }

    private void navigateToProductList(String filter) {
        // Créer un fragment de liste de produits avec le filtre spécifié
        ProductListFragment fragment = new ProductListFragment();
        if (filter != null) {
            Bundle args = new Bundle();
            args.putString("filter", filter);
            fragment.setArguments(args);
        }

        // Naviguer vers le fragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onProductClick(Product product) {
        // Naviguer vers le détail du produit
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
    public void onCategoryClick(Category category) {
        // Naviguer vers la liste de produits filtrée par catégorie
        navigateToProductList(category.getCategoryId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
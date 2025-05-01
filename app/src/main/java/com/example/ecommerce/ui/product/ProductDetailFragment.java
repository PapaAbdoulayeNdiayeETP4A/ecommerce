// ui/product/ProductDetailFragment.java
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.ProductAdapter;
import com.example.ecommerce.adapters.ProductImageAdapter;
import com.example.ecommerce.databinding.FragmentProductDetailBinding;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.viewmodels.CartViewModel;
import com.example.ecommerce.viewmodels.ProductViewModel;
import com.example.ecommerce.viewmodels.UserViewModel;
import com.example.ecommerce.viewmodels.WishlistViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductDetailFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FragmentProductDetailBinding binding;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private WishlistViewModel wishlistViewModel;
    private UserViewModel userViewModel;
    private String productId;
    private Product currentProduct;
    private int quantity = 1;
    private NumberFormat currencyFormatter;
    private ProductImageAdapter imageAdapter;
    private ProductAdapter similarProductsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
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

        // Initialiser le formatteur de devise
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        // Initialiser les ViewModels
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(requireActivity()).get(WishlistViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Configurer la toolbar
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Configurer les contrôles de quantité
        setupQuantityControls();

        // Configurer le bouton d'ajout au panier
        binding.btnAddToCart.setOnClickListener(v -> addToCart());

        // Charger les détails du produit
        loadProductDetails();
    }

    private void setupQuantityControls() {
        binding.tvQuantity.setText(String.valueOf(quantity));

        binding.btnDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        binding.btnIncreaseQuantity.setOnClickListener(v -> {
            if (currentProduct != null && quantity < currentProduct.getStockQuantity()) {
                quantity++;
                binding.tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(requireContext(), R.string.max_quantity_reached, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        productViewModel.setSelectedProductId(productId);

        productViewModel.getProductDetails().observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);

            if (response.isSuccess()) {
                Product product = response.getData();
                currentProduct = product;
                updateUI(product);
                setupProductImages(product);
                loadSimilarProducts(product.getCategory());
            } else {
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });

        // Vérifier si le produit est dans la liste de souhaits
        userViewModel.getCurrentFirebaseUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                wishlistViewModel.setCurrentUser(firebaseUser.getUid());
                wishlistViewModel.isInWishlist(productId).observe(getViewLifecycleOwner(), isInWishlist -> {
                    // Mettre à jour l'icône du bouton favori si nécessaire
                });
            }
        });
    }

    private void updateUI(Product product) {
        binding.tvProductName.setText(product.getName());
        binding.tvProductPrice.setText(currencyFormatter.format(product.getPrice()));

        if (product.isOnSale() && product.getSalePrice() > 0) {
            binding.tvOldPrice.setVisibility(View.VISIBLE);
            binding.tvDiscountPercentage.setVisibility(View.VISIBLE);
            binding.tvOldPrice.setText(currencyFormatter.format(product.getPrice()));
            binding.tvProductPrice.setText(currencyFormatter.format(product.getSalePrice()));

            // Calculer le pourcentage de réduction
            int discountPercentage = (int) (100 - (product.getSalePrice() * 100 / product.getPrice()));
            binding.tvDiscountPercentage.setText(String.format("-%d%%", discountPercentage));
        } else {
            binding.tvOldPrice.setVisibility(View.GONE);
            binding.tvDiscountPercentage.setVisibility(View.GONE);
        }

        binding.ratingBar.setRating(product.getRating());
        binding.tvReviewCount.setText(String.format(Locale.getDefault(), "(%d)", product.getReviewCount()));
        binding.tvProductDescription.setText(product.getDescription());
    }

    private void setupProductImages(Product product) {
        List<String> imageUrls = product.getImageUrls();
        if (imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = new ArrayList<>();
            // Ajouter au moins l'image principale
            if (product.getMainImageUrl() != null) {
                imageUrls.add(product.getMainImageUrl());
            } else {
                // Aucune image disponible
                return;
            }
        }

        imageAdapter = new ProductImageAdapter(requireContext(), imageUrls);
        binding.productImagePager.setAdapter(imageAdapter);

        // Configurer les indicateurs de page
        new TabLayoutMediator(binding.tabLayout, binding.productImagePager, (tab, position) -> {
            // Ne rien faire ici, les indicateurs sont visuels uniquement
        }).attach();
    }

    private void loadSimilarProducts(String category) {
        if (category != null) {
            similarProductsAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), this, ProductAdapter.VIEW_TYPE_HORIZONTAL);
            binding.rvSimilarProducts.setAdapter(similarProductsAdapter);
            binding.rvSimilarProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

            productViewModel.setCategory(category);
            productViewModel.getProductsByCategory().observe(getViewLifecycleOwner(), products -> {
                if (products != null && !products.isEmpty()) {
                    // Filtrer pour exclure le produit actuel et limiter à 5 produits
                    List<Product> similarProducts = new ArrayList<>();
                    for (Product p : products) {
                        if (!p.getProductId().equals(productId)) {
                            similarProducts.add(p);
                            if (similarProducts.size() >= 5) break;
                        }
                    }
                    similarProductsAdapter.updateProducts(similarProducts);
                }
            });
        }
    }

    private void addToCart() {
        if (currentProduct == null) return;

        userViewModel.getCurrentFirebaseUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                Toast.makeText(requireContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
                return;
            }

            cartViewModel.setCurrentUser(firebaseUser.getUid());

            // Créer un nouvel élément de panier
            CartItem cartItem = new CartItem();
            cartItem.setProductId(currentProduct.getProductId());
            cartItem.setUserId(firebaseUser.getUid());
            cartItem.setQuantity(quantity);
            // Utiliser le prix de vente s'il est en promotion
            double price = currentProduct.isOnSale() ? currentProduct.getSalePrice() : currentProduct.getPrice();
            cartItem.setPrice(price);

            // Ajouter au panier
            cartViewModel.addToCart(cartItem).observe(getViewLifecycleOwner(), response -> {
                if (response.isSuccess()) {
                    Toast.makeText(requireContext(), R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onProductClick(Product product) {
        // Naviguer vers les détails du produit cliqué (produit similaire)
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
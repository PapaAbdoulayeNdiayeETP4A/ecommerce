// ui/wishlist/WishlistFragment.java
package com.example.ecommerce.ui.wishlist;

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

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.WishlistAdapter;
import com.example.ecommerce.databinding.FragmentWishlistBinding;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.models.Wishlist;
import com.example.ecommerce.ui.product.ProductDetailFragment;
import com.example.ecommerce.viewmodels.ProductViewModel;
import com.example.ecommerce.viewmodels.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment implements WishlistAdapter.WishlistItemListener {

    private FragmentWishlistBinding binding;
    private WishlistViewModel wishlistViewModel;
    private ProductViewModel productViewModel;
    private WishlistAdapter adapter;
    private List<Product> wishlistProducts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les ViewModels
        wishlistViewModel = new ViewModelProvider(requireActivity()).get(WishlistViewModel.class);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Configurer l'adaptateur
        adapter = new WishlistAdapter(requireContext(), wishlistProducts, this);
        binding.rvWishlist.setAdapter(adapter);
        binding.rvWishlist.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Observer la liste de souhaits
        wishlistViewModel.getUserWishlist().observe(getViewLifecycleOwner(), this::processWishlistItems);

        // Configurer le SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Rafraîchir la liste de souhaits
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void processWishlistItems(List<Wishlist> wishlistItems) {
        if (wishlistItems == null || wishlistItems.isEmpty()) {
            showEmptyView();
            return;
        }

        // Récupérer les détails des produits pour chaque élément de la liste de souhaits
        wishlistProducts.clear();

        for (Wishlist item : wishlistItems) {
            // Dans une application réelle, vous feriez un appel à l'API ou à la base de données
            // pour obtenir les détails du produit à partir de l'ID du produit
            // Pour cet exemple, nous créons des produits fictifs
            Product product = createDummyProduct(item.getProductId());
            wishlistProducts.add(product);
        }

        updateUI();
    }

    private Product createDummyProduct(String productId) {
        // Créer un produit fictif pour l'exemple
        // Dans une application réelle, ces données viendraient de la base de données ou de l'API
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Produit " + productId);
        product.setPrice(99.99);
        product.setMainImageUrl("https://via.placeholder.com/150");
        return product;
    }

    private void updateUI() {
        if (wishlistProducts.isEmpty()) {
            showEmptyView();
        } else {
            adapter.updateProducts(wishlistProducts);
            binding.rvWishlist.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView() {
        binding.rvWishlist.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProductClick(Product product) {
        // Naviguer vers l'écran de détails du produit
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
    public void onRemoveFromWishlist(Product product) {
        wishlistViewModel.removeFromWishlist(product.getProductId()).observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                // Produit supprimé avec succès
                wishlistProducts.remove(product);
                updateUI();
                Toast.makeText(requireContext(), R.string.product_removed_from_wishlist, Toast.LENGTH_SHORT).show();
            } else {
                // Échec de la suppression
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCart(Product product) {
        // Logique pour ajouter au panier
        Toast.makeText(requireContext(), R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
// ui/cart/CartFragment.java
package com.example.ecommerce.ui.cart;

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

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.CartAdapter;
import com.example.ecommerce.databinding.FragmentCartBinding;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.ui.payment.CheckoutActivity;
import com.example.ecommerce.viewmodels.CartViewModel;
import com.example.ecommerce.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;
    private CartAdapter cartAdapter;
    private String currentUserId;
    private NumberFormat currencyFormatter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le formatteur de devise
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        // Initialiser les ViewModels
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Configurer l'adaptateur
        cartAdapter = new CartAdapter(requireContext(), new ArrayList<>(), this);
        binding.rvCartItems.setAdapter(cartAdapter);

        // Observer l'utilisateur actuel
        userViewModel.getCurrentFirebaseUser().observe(getViewLifecycleOwner(), this::updateUserId);

        // Configurer le bouton de paiement
        binding.btnCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void updateUserId(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            cartViewModel.setCurrentUser(currentUserId);

            // Observer les articles du panier
            cartViewModel.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);

            // Observer le total du panier
            cartViewModel.getCartTotal().observe(getViewLifecycleOwner(), this::updateCartTotal);
        } else {
            // L'utilisateur n'est pas connecté
            showEmptyCart();
            binding.checkoutPanel.setVisibility(View.GONE);
        }
    }

    private void updateCartItems(List<CartItem> cartItems) {
        if (cartItems != null && !cartItems.isEmpty()) {
            cartAdapter.updateCartItems(cartItems);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.tvEmptyCart.setVisibility(View.GONE);
            binding.checkoutPanel.setVisibility(View.VISIBLE);

            // Calculer et afficher les valeurs
            updateCartSummary(cartItems);
        } else {
            showEmptyCart();
        }
    }

    private void showEmptyCart() {
        binding.rvCartItems.setVisibility(View.GONE);
        binding.tvEmptyCart.setVisibility(View.VISIBLE);
        binding.checkoutPanel.setVisibility(View.GONE);
    }

    private void updateCartSummary(List<CartItem> cartItems) {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double shipping = subtotal > 0 ? 9.99 : 0;
        double tax = subtotal * 0.1; // 10% de taxe
        double total = subtotal + shipping + tax;

        binding.tvSubtotalValue.setText(currencyFormatter.format(subtotal));
        binding.tvShippingValue.setText(currencyFormatter.format(shipping));
        binding.tvTaxValue.setText(currencyFormatter.format(tax));
        binding.tvTotalValue.setText(currencyFormatter.format(total));
    }

    private void updateCartTotal(Double total) {
        // Cette méthode est utilisée pour mettre à jour le total du panier depuis le ViewModel
        // Mais comme nous calculons déjà le total nous-mêmes, cette méthode est facultative
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        cartItem.setQuantity(newQuantity);
        cartViewModel.updateCartItem(String.valueOf(cartItem.getId()), cartItem).observe(getViewLifecycleOwner(), response -> {
            if (!response.isSuccess()) {
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemRemoved(CartItem cartItem) {
        cartViewModel.removeCartItem(String.valueOf(cartItem.getId())).observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                Toast.makeText(requireContext(), R.string.item_removed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToCheckout() {
        if (currentUserId != null) {
            Intent intent = new Intent(requireActivity(), CheckoutActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
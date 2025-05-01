// ui/payment/CheckoutActivity.java
package com.example.ecommerce.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCheckoutBinding;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.User;
import com.example.ecommerce.viewmodels.CartViewModel;
import com.example.ecommerce.viewmodels.OrderViewModel;
import com.example.ecommerce.viewmodels.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;
    private UserViewModel userViewModel;
    private User currentUser;
    private double subtotal, shipping, tax, total;
    private int itemCount;
    private NumberFormat currencyFormatter;
    private String paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser le formatteur de devise
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        // Initialiser les ViewModels
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Configurer la toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Observer l'utilisateur actuel
        userViewModel.getCurrentFirebaseUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                userViewModel.setCurrentUser(firebaseUser.getUid());
                cartViewModel.setCurrentUser(firebaseUser.getUid());
                orderViewModel.setCurrentUser(firebaseUser.getUid());
            } else {
                // L'utilisateur n'est pas connecté, retour à l'écran précédent
                finish();
            }
        });

        // Observer le profil utilisateur
        userViewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                updateUserInfo();
            }
        });

        // Observer les articles du panier
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems != null) {
                itemCount = cartItems.size();
                updateOrderSummary();
            }
        });

        // Observer le total du panier
        cartViewModel.getCartTotal().observe(this, cartTotal -> {
            if (cartTotal != null) {
                subtotal = cartTotal;
                shipping = subtotal > 0 ? 9.99 : 0;
                tax = subtotal * 0.1; // 10% de taxe
                total = subtotal + shipping + tax;
                updateOrderSummary();
            }
        });

        // Configurer les écouteurs de clics
        binding.tvEditAddress.setOnClickListener(v -> editAddress());
        binding.btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Définir la méthode de paiement par défaut
        paymentMethod = "Credit Card";
        binding.rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                if (checkedId == R.id.rb_credit_card) {
                    paymentMethod = "Credit Card";
                } else if (checkedId == R.id.rb_paypal) {
                    paymentMethod = "PayPal";
                } else if (checkedId == R.id.rb_cash_on_delivery) {
                    paymentMethod = "Cash on Delivery";
                }
            }
        });
    }

    private void updateUserInfo() {
        binding.tvName.setText(currentUser.getName());
        binding.tvAddress.setText(currentUser.getAddress());
        binding.tvPhone.setText(currentUser.getPhone());
    }

    private void updateOrderSummary() {
        binding.tvItemsCount.setText(getString(R.string.item_count, itemCount));
        binding.tvSubtotalValue.setText(currencyFormatter.format(subtotal));
        binding.tvShippingValue.setText(currencyFormatter.format(shipping));
        binding.tvTaxValue.setText(currencyFormatter.format(tax));
        binding.tvTotalValue.setText(currencyFormatter.format(total));
    }

    private void editAddress() {
        // Ici, vous pourriez lancer une activité pour modifier l'adresse
        Toast.makeText(this, "Cette fonctionnalité n'est pas encore implémentée", Toast.LENGTH_SHORT).show();
    }

    private void placeOrder() {
        // Afficher la boîte de dialogue de confirmation
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_order)
                .setMessage(R.string.confirm_order_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    processOrder();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void processOrder() {
        // Afficher la barre de progression
        binding.progressBar.setVisibility(View.VISIBLE);

        // Créer un nouvel objet Order
        Order order = new Order();
        order.setUserId(currentUser.getUserId());
        order.setOrderDate(new Date());
        order.setTotalAmount(total);
        order.setStatus("pending");
        order.setShippingAddress(currentUser.getAddress());
        order.setPaymentMethod(paymentMethod);

        // Passer la commande
        orderViewModel.placeOrder(order).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);

            if (response.isSuccess()) {
                // Vider le panier
                cartViewModel.clearCart();

                // Rediriger vers l'écran de confirmation
                Intent intent = new Intent(this, OrderConfirmationActivity.class);
                intent.putExtra("order", response.getData());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, response.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
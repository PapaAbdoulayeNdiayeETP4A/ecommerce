// ui/payment/OrderConfirmationActivity.java
package com.example.ecommerce.ui.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityOrderConfirmationBinding;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.ui.home.MainActivity;
import com.example.ecommerce.ui.orders.OrdersActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

public class OrderConfirmationActivity extends AppCompatActivity {

    private ActivityOrderConfirmationBinding binding;
    private Order order;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser les formatteurs
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Récupérer la commande passée en paramètre
        if (getIntent().hasExtra("order")) {
            order = (Order) getIntent().getSerializableExtra("order");
            updateOrderInfo();
        } else {
            // Aucune commande n'a été transmise, terminer l'activité
            finish();
            return;
        }

        // Configurer la toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> navigateToHome());

        // Configurer les boutons
        binding.btnViewOrders.setOnClickListener(v -> navigateToOrders());
        binding.btnContinueShopping.setOnClickListener(v -> navigateToHome());
    }

    private void updateOrderInfo() {
        binding.tvOrderNumber.setText(order.getOrderId());
        binding.tvOrderDate.setText(dateFormatter.format(order.getOrderDate()));
        binding.tvPaymentMethod.setText(order.getPaymentMethod());
        binding.tvOrderTotal.setText(currencyFormatter.format(order.getTotalAmount()));
    }

    private void navigateToOrders() {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Rediriger vers l'écran d'accueil au lieu de revenir en arrière
        navigateToHome();
    }
}
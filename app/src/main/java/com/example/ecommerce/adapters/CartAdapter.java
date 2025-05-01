package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Product;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartItems;
    private final CartItemListener listener;
    private final NumberFormat currencyFormatter;

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;

        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public interface CartItemListener {
        void onQuantityChanged(CartItem cartItem, int newQuantity);
        void onItemRemoved(CartItem cartItem);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;
        private final ImageButton btnDecrease;
        private final ImageButton btnIncrease;
        private final ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CartItem cartItem) {
            // Chargement de l'image du produit
            if (cartItem.getProduct() != null && cartItem.getProduct().getMainImageUrl() != null) {
                Glide.with(context)
                        .load(cartItem.getProduct().getMainImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(ivProduct);
            } else {
                // Image par défaut si aucune image n'est disponible
                ivProduct.setImageResource(R.drawable.placeholder_image);
            }

            // Définir les données
            Product product = cartItem.getProduct();
            String productName = product != null ? product.getName() : "Product";
            tvProductName.setText(productName);
            tvProductPrice.setText(currencyFormatter.format(cartItem.getPrice()));
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvSubtotal.setText(currencyFormatter.format(cartItem.getSubtotal()));

            // Configurer les écouteurs de clics
            btnDecrease.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                if (currentQuantity > 1) {
                    listener.onQuantityChanged(cartItem, currentQuantity - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                // On peut limiter la quantité maximale si nécessaire
                listener.onQuantityChanged(cartItem, currentQuantity + 1);
            });

            btnRemove.setOnClickListener(v -> listener.onItemRemoved(cartItem));
        }
    }
}
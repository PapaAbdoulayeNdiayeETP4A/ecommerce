package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.Product;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_GRID = 1;
    public static final int VIEW_TYPE_HORIZONTAL = 2;

    private final Context context;
    private List<Product> products;
    private final OnProductClickListener listener;
    private final int viewType;
    private final NumberFormat currencyFormatter;

    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener, int viewType) {
        this.context = context;
        this.products = products;
        this.listener = listener;
        this.viewType = viewType;

        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.viewType == VIEW_TYPE_HORIZONTAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_horizontal, parent, false);
            return new HorizontalViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
            return new GridViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = products.get(position);
        if (holder instanceof GridViewHolder) {
            ((GridViewHolder) holder).bind(product);
        } else if (holder instanceof HorizontalViewHolder) {
            ((HorizontalViewHolder) holder).bind(product);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvOldPrice;
        private final ImageButton btnFavorite;
        private final ImageButton btnAddToCart;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(products.get(position));
                }
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormatter.format(product.getPrice()));

            if (product.isOnSale() && product.getSalePrice() > 0) {
                tvOldPrice.setVisibility(View.VISIBLE);
                tvOldPrice.setText(currencyFormatter.format(product.getPrice()));
                tvProductPrice.setText(currencyFormatter.format(product.getSalePrice()));
            } else {
                tvOldPrice.setVisibility(View.GONE);
            }

            // Charger l'image du produit
            if (product.getMainImageUrl() != null && !product.getMainImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(product.getMainImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(ivProduct);
            } else {
                // Image par défaut si aucune image n'est disponible
                ivProduct.setImageResource(R.drawable.placeholder_image);
            }

            // Configurer les boutons
            btnFavorite.setOnClickListener(v -> {
                // Logique pour ajouter/retirer des favoris
            });

            btnAddToCart.setOnClickListener(v -> {
                // Logique pour ajouter au panier
            });
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvOldPrice;
        private final RatingBar ratingBar;
        private final ImageButton btnFavorite;
        private final ImageButton btnAddToCart;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(products.get(position));
                }
            });
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormatter.format(product.getPrice()));

            if (product.isOnSale() && product.getSalePrice() > 0) {
                tvOldPrice.setVisibility(View.VISIBLE);
                tvOldPrice.setText(currencyFormatter.format(product.getPrice()));
                tvProductPrice.setText(currencyFormatter.format(product.getSalePrice()));
            } else {
                tvOldPrice.setVisibility(View.GONE);
            }

            // Définir la note
            ratingBar.setRating(product.getRating());

            // Charger l'image du produit
            if (product.getMainImageUrl() != null && !product.getMainImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(product.getMainImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(ivProduct);
            } else {
                // Image par défaut si aucune image n'est disponible
                ivProduct.setImageResource(R.drawable.placeholder_image);
            }

            // Configurer les boutons
            btnFavorite.setOnClickListener(v -> {
                // Logique pour ajouter/retirer des favoris
            });

            btnAddToCart.setOnClickListener(v -> {
                // Logique pour ajouter au panier
            });
        }
    }
}
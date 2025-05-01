package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.OrderItem;
import com.example.ecommerce.models.Product;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final Context context;
    private List<OrderItem> orderItems;
    private final NumberFormat currencyFormatter;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;

        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void updateOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        notifyDataSetChanged();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
        }

        public void bind(OrderItem orderItem) {
            tvProductName.setText(orderItem.getProductName());
            tvProductPrice.setText(currencyFormatter.format(orderItem.getPrice()));
            tvQuantity.setText(context.getString(R.string.quantity_format, orderItem.getQuantity()));
            tvSubtotal.setText(currencyFormatter.format(orderItem.getPrice() * orderItem.getQuantity()));

            // Charger l'image du produit si disponible
            Product product = orderItem.getProduct();
            if (product != null && product.getMainImageUrl() != null) {
                Glide.with(context)
                        .load(product.getMainImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(ivProduct);
            } else {
                // Image par défaut si aucune image n'est disponible
                ivProduct.setImageResource(R.drawable.placeholder_image);
            }
        }
    }
}
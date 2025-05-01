package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.models.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orders;
    private final OnOrderClickListener listener;
    private final NumberFormat currencyFormatter;
    private final SimpleDateFormat dateFormatter;

    public OrderAdapter(Context context, List<Order> orders, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;

        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvOrderDate;
        private final TextView tvOrderStatus;
        private final TextView tvOrderTotal;
        private final TextView tvItemCount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(orders.get(position));
                }
            });
        }

        public void bind(Order order) {
            tvOrderId.setText(context.getString(R.string.order_id_format, order.getOrderId()));
            tvOrderDate.setText(dateFormatter.format(order.getOrderDate()));
            tvOrderStatus.setText(getFormattedStatus(order.getStatus()));
            tvOrderTotal.setText(currencyFormatter.format(order.getTotalAmount()));

            int itemCount = order.getItems() != null ? order.getItems().size() : 0;
            tvItemCount.setText(context.getResources().getQuantityString(R.plurals.item_count, itemCount, itemCount));

            // Définir la couleur du statut
            int statusColor;
            switch (order.getStatus().toLowerCase()) {
                case "pending":
                    statusColor = context.getResources().getColor(R.color.status_pending);
                    break;
                case "processing":
                    statusColor = context.getResources().getColor(R.color.status_processing);
                    break;
                case "shipped":
                    statusColor = context.getResources().getColor(R.color.status_shipped);
                    break;
                case "delivered":
                    statusColor = context.getResources().getColor(R.color.status_delivered);
                    break;
                case "cancelled":
                    statusColor = context.getResources().getColor(R.color.status_cancelled);
                    break;
                default:
                    statusColor = context.getResources().getColor(R.color.text_secondary);
                    break;
            }
            tvOrderStatus.setTextColor(statusColor);
        }

        private String getFormattedStatus(String status) {
            if (status == null || status.isEmpty()) return "";

            // Première lettre en majuscule, reste en minuscule
            return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        }
    }
}
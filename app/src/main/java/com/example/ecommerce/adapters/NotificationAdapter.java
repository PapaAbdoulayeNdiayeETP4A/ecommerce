package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.models.Notification;
import com.example.ecommerce.utils.DateUtils;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private List<Notification> notifications;
    private final OnNotificationClickListener listener;

    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final TextView tvMessage;
        private final TextView tvTime;
        private final View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            unreadIndicator = itemView.findViewById(R.id.view_unread_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNotificationClick(notifications.get(position));
                }
            });
        }

        public void bind(Notification notification) {
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
            tvTime.setText(DateUtils.getRelativeTimeSpan(new Date(notification.getTimestamp())));

            // Définir la visibilité de l'indicateur non lu
            unreadIndicator.setVisibility(notification.isRead() ? View.INVISIBLE : View.VISIBLE);

            // Définir l'arrière-plan en fonction de l'état de lecture
            int backgroundColor = notification.isRead()
                    ? ContextCompat.getColor(context, R.color.notification_read_background)
                    : ContextCompat.getColor(context, R.color.notification_unread_background);
            itemView.setBackgroundColor(backgroundColor);

            // Définir l'icône en fonction du type de notification
            int iconResource = getIconResourceForType(notification.getType());
            ivIcon.setImageResource(iconResource);
        }

        private int getIconResourceForType(String type) {
            switch (type) {
                case "order":
                    return R.drawable.ic_notification_order;
                case "promo":
                    return R.drawable.ic_notification_promo;
                case "product":
                    return R.drawable.ic_notification_product;
                case "system":
                    return R.drawable.ic_notification_system;
                default:
                    return R.drawable.ic_notification_default;
            }
        }
    }
}
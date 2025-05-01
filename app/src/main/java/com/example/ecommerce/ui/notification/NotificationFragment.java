// ui/notification/NotificationFragment.java
package com.example.ecommerce.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.NotificationAdapter;
import com.example.ecommerce.databinding.FragmentNotificationBinding;
import com.example.ecommerce.models.Notification;
import com.example.ecommerce.viewmodels.NotificationViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private FragmentNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        // Configurer l'adaptateur
        notificationAdapter = new NotificationAdapter(requireContext(), new ArrayList<>(), this);
        binding.rvNotifications.setAdapter(notificationAdapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observer les notifications de l'utilisateur
        notificationViewModel.getUserNotifications().observe(getViewLifecycleOwner(), this::updateNotifications);

        // Configurer le SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Actualiser les notifications
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void updateNotifications(List<Notification> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            notificationAdapter.updateNotifications(notifications);
            binding.rvNotifications.setVisibility(View.VISIBLE);
            binding.tvEmptyNotifications.setVisibility(View.GONE);
        } else {
            binding.rvNotifications.setVisibility(View.GONE);
            binding.tvEmptyNotifications.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notifications, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mark_all_read) {
            notificationViewModel.markAllAsRead();
            return true;
        } else if (id == R.id.action_clear_all) {
            showDeleteAllConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_all_notifications)
                .setMessage(R.string.clear_all_notifications_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    notificationViewModel.deleteAllNotifications();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Marquer la notification comme lue
        notificationViewModel.markAsRead(notification.getId());

        // Naviguer vers l'écran approprié selon le type de notification
        navigateBasedOnNotificationType(notification);
    }

    private void navigateBasedOnNotificationType(Notification notification) {
        // Naviguer vers l'écran approprié selon le type et l'ID de référence de la notification
        String type = notification.getType();
        String referenceId = notification.getReferenceId();

        if ("order".equals(type)) {
            // Naviguer vers les détails de la commande
            // navigateToOrderDetails(referenceId);
        } else if ("promo".equals(type)) {
            // Naviguer vers les détails de la promotion
            // navigateToPromoDetails(referenceId);
        } else if ("product".equals(type)) {
            // Naviguer vers les détails du produit
            // navigateToProductDetails(referenceId);
        }
        // Ajouter d'autres types selon les besoins
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
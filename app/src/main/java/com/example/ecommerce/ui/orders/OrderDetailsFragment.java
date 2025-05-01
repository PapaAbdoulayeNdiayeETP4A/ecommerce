// ui/orders/OrderDetailsFragment.java
package com.example.ecommerce.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.OrderItemAdapter;
import com.example.ecommerce.databinding.FragmentOrderDetailsBinding;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.OrderItem;
import com.example.ecommerce.utils.CurrencyUtils;
import com.example.ecommerce.utils.DateUtils;
import com.example.ecommerce.viewmodels.OrderDetailsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsFragment extends Fragment {

    private FragmentOrderDetailsBinding binding;
    private OrderDetailsViewModel viewModel;
    private OrderItemAdapter adapter;
    private String orderId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer l'ID de commande des arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("orderId")) {
            orderId = args.getString("orderId");
        } else {
            // Aucun ID de commande, revenir en arrière
            Toast.makeText(requireContext(), R.string.error_order_not_found, Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.setSelectedOrder(orderId);

        // Configurer l'adaptateur pour les articles de la commande
        adapter = new OrderItemAdapter(requireContext(), new ArrayList<>());
        binding.rvOrderItems.setAdapter(adapter);
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observer les détails de la commande
        viewModel.getOrderDetails().observe(getViewLifecycleOwner(), this::updateOrderDetails);

        // Observer les articles de la commande
        viewModel.getOrderItems().observe(getViewLifecycleOwner(), this::updateOrderItems);

        // Configurer les boutons d'action
        setupActionButtons();
    }

    private void updateOrderDetails(Order order) {
        if (order != null) {
            // Mettre à jour l'en-tête
            binding.tvOrderNumber.setText(getString(R.string.order_number_format, order.getOrderId()));
            binding.tvOrderDate.setText(DateUtils.formatDate(order.getOrderDate()));
            binding.tvOrderStatus.setText(getFormattedStatus(order.getStatus()));

            // Définir la couleur du statut
            int statusColor = getStatusColor(order.getStatus());
            binding.tvOrderStatus.setTextColor(statusColor);

            // Mettre à jour les détails de livraison
            binding.tvShippingAddress.setText(order.getShippingAddress());
            if (order.getTrackingNumber() != null && !order.getTrackingNumber().isEmpty()) {
                binding.tvTrackingNumber.setText(order.getTrackingNumber());
                binding.layoutTrackingNumber.setVisibility(View.VISIBLE);
            } else {
                binding.layoutTrackingNumber.setVisibility(View.GONE);
            }

            // Mettre à jour les détails de paiement
            binding.tvPaymentMethod.setText(order.getPaymentMethod());
            binding.tvOrderTotal.setText(CurrencyUtils.formatCurrency(order.getTotalAmount()));

            // Afficher/masquer les boutons en fonction du statut
            updateActionButtonsVisibility(order.getStatus());
        } else {
            // Commande non trouvée
            Toast.makeText(requireContext(), R.string.error_order_not_found, Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void updateOrderItems(List<OrderItem> items) {
        if (items != null && !items.isEmpty()) {
            adapter.updateOrderItems(items);
            binding.rvOrderItems.setVisibility(View.VISIBLE);
            binding.tvNoItems.setVisibility(View.GONE);
        } else {
            binding.rvOrderItems.setVisibility(View.GONE);
            binding.tvNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void setupActionButtons() {
        // Bouton d'annulation de commande
        binding.btnCancelOrder.setOnClickListener(v -> {
            showCancelOrderConfirmationDialog();
        });

        // Bouton de demande de remboursement
        binding.btnRequestRefund.setOnClickListener(v -> {
            showRefundRequestDialog();
        });

        // Bouton de suivi de la commande
        binding.btnTrackOrder.setOnClickListener(v -> {
            // Rediriger vers le suivi de commande
            Toast.makeText(requireContext(), R.string.tracking_not_available, Toast.LENGTH_SHORT).show();
        });

        // Bouton de contact du service client
        binding.btnContactSupport.setOnClickListener(v -> {
            // Rediriger vers le support
            Toast.makeText(requireContext(), R.string.customer_support_not_available, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateActionButtonsVisibility(String status) {
        // Logique pour afficher/masquer les boutons en fonction du statut de la commande
        if (status != null) {
            switch (status.toLowerCase()) {
                case "pending":
                case "processing":
                    binding.btnCancelOrder.setVisibility(View.VISIBLE);
                    binding.btnRequestRefund.setVisibility(View.GONE);
                    binding.btnTrackOrder.setVisibility(View.GONE);
                    break;
                case "shipped":
                    binding.btnCancelOrder.setVisibility(View.GONE);
                    binding.btnRequestRefund.setVisibility(View.GONE);
                    binding.btnTrackOrder.setVisibility(View.VISIBLE);
                    break;
                case "delivered":
                    binding.btnCancelOrder.setVisibility(View.GONE);
                    binding.btnRequestRefund.setVisibility(View.VISIBLE);
                    binding.btnTrackOrder.setVisibility(View.GONE);
                    break;
                case "cancelled":
                    binding.btnCancelOrder.setVisibility(View.GONE);
                    binding.btnRequestRefund.setVisibility(View.GONE);
                    binding.btnTrackOrder.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void showCancelOrderConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.cancel_order)
                .setMessage(R.string.cancel_order_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    cancelOrder();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void cancelOrder() {
        viewModel.cancelOrder().observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                Toast.makeText(requireContext(), R.string.order_cancelled_success, Toast.LENGTH_SHORT).show();
                updateOrderDetails(response.getData());
            } else {
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRefundRequestDialog() {
        // Dialog pour saisir la raison du remboursement
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_refund_request, null);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.request_refund)
                .setView(view)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    // Obtenir la raison du remboursement depuis le champ de texte
                    String reason = "Raison du remboursement"; // À remplacer par la récupération réelle
                    requestRefund(reason);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void requestRefund(String reason) {
        viewModel.requestRefund(reason).observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                Toast.makeText(requireContext(), R.string.refund_request_submitted, Toast.LENGTH_SHORT).show();
                updateOrderDetails(response.getData());
            } else {
                Toast.makeText(requireContext(), response.getError(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFormattedStatus(String status) {
        if (status == null || status.isEmpty()) return "";

        // Première lettre en majuscule, reste en minuscule
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }

    private int getStatusColor(String status) {
        int colorResId;
        switch (status.toLowerCase()) {
            case "pending":
                colorResId = R.color.status_pending;
                break;
            case "processing":
                colorResId = R.color.status_processing;
                break;
            case "shipped":
                colorResId = R.color.status_shipped;
                break;
            case "delivered":
                colorResId = R.color.status_delivered;
                break;
            case "cancelled":
                colorResId = R.color.status_cancelled;
                break;
            default:
                colorResId = R.color.text_secondary;
                break;
        }
        return requireContext().getResources().getColor(colorResId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
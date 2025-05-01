// ui/orders/OrdersFragment.java
package com.example.ecommerce.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.OrderAdapter;
import com.example.ecommerce.databinding.FragmentOrdersBinding;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.viewmodels.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private FragmentOrdersBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter orderAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // Configurer l'adaptateur
        orderAdapter = new OrderAdapter(requireContext(), new ArrayList<>(), this);
        binding.rvOrders.setAdapter(orderAdapter);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observer les commandes de l'utilisateur
        orderViewModel.getUserOrders().observe(getViewLifecycleOwner(), this::updateOrders);

        // Configurer le SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Rafraîchir les commandes
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void updateOrders(List<Order> orders) {
        if (orders != null && !orders.isEmpty()) {
            orderAdapter.updateOrders(orders);
            binding.rvOrders.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        } else {
            binding.rvOrders.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOrderClick(Order order) {
        // Naviguer vers les détails de la commande
        navigateToOrderDetails(order);
    }

    private void navigateToOrderDetails(Order order) {
        // Créer le fragment de détails de commande
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putString("orderId", order.getOrderId());
        fragment.setArguments(args);

        // Naviguer vers le fragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
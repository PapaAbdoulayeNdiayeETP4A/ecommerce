// ui/product/CategoriesFragment.java
package com.example.ecommerce.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.CategoryAdapter;
import com.example.ecommerce.databinding.FragmentCategoriesBinding;
import com.example.ecommerce.models.Category;
import com.example.ecommerce.viewmodels.CategoryViewModel;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private FragmentCategoriesBinding binding;
    private CategoryViewModel categoryViewModel;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        // Configurer l'adaptateur
        categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<>(), this);
        binding.rvCategories.setAdapter(categoryAdapter);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Observer les catégories
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), this::updateCategories);

        // Configurer la toolbar
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void updateCategories(List<Category> categories) {
        binding.progressBar.setVisibility(View.GONE);

        if (categories != null && !categories.isEmpty()) {
            categoryAdapter.updateCategories(categories);
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        // Naviguer vers la liste de produits de cette catégorie
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", category.getCategoryId());
        fragment.setArguments(args);

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
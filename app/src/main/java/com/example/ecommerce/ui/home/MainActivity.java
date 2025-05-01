// ui/home/MainActivity.java (version complète avec gestion des notifications)
package com.example.ecommerce.ui.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.notification.NotificationFragment;
import com.example.ecommerce.ui.orders.OrdersFragment;
import com.example.ecommerce.ui.product.CategoriesFragment;
import com.example.ecommerce.ui.profile.ProfileFragment;
import com.example.ecommerce.ui.wishlist.WishlistFragment;
import com.example.ecommerce.viewmodels.NotificationViewModel;
import com.example.ecommerce.viewmodels.UserViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private NotificationViewModel notificationViewModel;
    private BadgeDrawable notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialiser les ViewModels
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Observer les changements d'utilisateur
        userViewModel.getCurrentFirebaseUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                userViewModel.setCurrentUser(firebaseUser.getUid());
                notificationViewModel.setCurrentUser(firebaseUser.getUid());

                // Mettre à jour l'UI pour l'utilisateur connecté
                updateNavigationHeader(firebaseUser.getDisplayName(), firebaseUser.getEmail());
            } else {
                // Rediriger vers l'écran de connexion si nécessaire
                // Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // startActivity(intent);
                // finish();
            }
        });

        // Observer le nombre de notifications non lues
        notificationViewModel.getUnreadNotificationCount().observe(this, count -> {
            if (count != null && count > 0) {
                if (notificationBadge == null) {
                    notificationBadge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notification);
                }
                notificationBadge.setVisible(true);
                notificationBadge.setNumber(count);
            } else if (notificationBadge != null) {
                notificationBadge.setVisible(false);
            }
        });

        // Configurer la navigation
        setupNavigation();

        // Charger le fragment d'accueil par défaut
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void setupNavigation() {
        // Configurer le menu du bas
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_categories) {
                fragment = new CategoriesFragment();
            } else if (itemId == R.id.nav_cart) {
                fragment = new CartFragment();
            } else if (itemId == R.id.nav_notification) {
                fragment = new NotificationFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });

        // Configurer le navigation drawer (menu latéral)
        binding.navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateNavigationHeader(String name, String email) {
        // Mettre à jour les informations utilisateur dans le header du navigation drawer
        View headerView = binding.navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_user_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_user_email);

        if (tvName != null) tvName.setText(name);
        if (tvEmail != null) tvEmail.setText(email);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Gestion des clics sur les éléments du menu latéral
        Fragment fragment = null;
        boolean shouldCloseDrawer = true;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_wishlist) {
            fragment = new WishlistFragment();
        } else if (itemId == R.id.nav_orders) {
            fragment = new OrdersFragment();
        } else if (itemId == R.id.nav_settings) {
            // Lancer l'activité des paramètres
            // Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            // startActivity(intent);
            shouldCloseDrawer = false;
        } else if (itemId == R.id.nav_logout) {
            // Déconnexion
            FirebaseAuth.getInstance().signOut();
            userViewModel.logout();
            shouldCloseDrawer = false;
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        if (shouldCloseDrawer) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // Fermer le drawer s'il est ouvert
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
package com.example.ecommerce.api;

import com.example.ecommerce.models.Address;
import com.example.ecommerce.models.Category;
import com.example.ecommerce.models.Notification;
import com.example.ecommerce.models.PaymentMethod;
import com.example.ecommerce.models.Review;
import com.example.ecommerce.models.Wishlist;
import com.example.ecommerce.models.User;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.CartItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoints pour les adresses
    @GET("addresses/{userId}")
    Call<List<Address>> getUserAddresses(@Path("userId") String userId);

    @POST("addresses")
    Call<Address> addAddress(@Body Address address);

    @PUT("addresses/{addressId}")
    Call<Address> updateAddress(@Path("addressId") String addressId, @Body Address address);

    @DELETE("addresses/{addressId}")
    Call<Void> deleteAddress(@Path("addressId") String addressId);

    // Endpoints pour les avis
    @GET("reviews/product/{productId}")
    Call<List<Review>> getProductReviews(@Path("productId") String productId);

    @GET("reviews/user/{userId}")
    Call<List<Review>> getUserReviews(@Path("userId") String userId);

    @POST("reviews")
    Call<Review> addReview(@Body Review review);

    @PUT("reviews/{reviewId}")
    Call<Review> updateReview(@Path("reviewId") String reviewId, @Body Review review);

    @DELETE("reviews/{reviewId}")
    Call<Void> deleteReview(@Path("reviewId") String reviewId);

    // Endpoints pour la liste de souhaits
    @GET("wishlist/{userId}")
    Call<List<Wishlist>> getUserWishlist(@Path("userId") String userId);

    @POST("wishlist")
    Call<Void> addToWishlist(@Body Wishlist wishlistItem);

    @DELETE("wishlist/{userId}/{productId}")
    Call<Void> removeFromWishlist(@Path("userId") String userId, @Path("productId") String productId);

    // Endpoints pour les notifications
    @GET("notifications/{userId}")
    Call<List<Notification>> getUserNotifications(@Path("userId") String userId);

    @PUT("notifications/{userId}/read-all")
    Call<Void> markAllNotificationsAsRead(@Path("userId") String userId);

    @DELETE("notifications/{userId}")
    Call<Void> deleteAllNotifications(@Path("userId") String userId);

    // Endpoints pour les méthodes de paiement
    @GET("payment-methods/{userId}")
    Call<List<PaymentMethod>> getUserPaymentMethods(@Path("userId") String userId);

    @POST("payment-methods")
    Call<PaymentMethod> addPaymentMethod(@Body PaymentMethod paymentMethod);

    @PUT("payment-methods/{paymentMethodId}")
    Call<PaymentMethod> updatePaymentMethod(@Path("paymentMethodId") String paymentMethodId, @Body PaymentMethod paymentMethod);

    @DELETE("payment-methods/{paymentMethodId}")
    Call<Void> deletePaymentMethod(@Path("paymentMethodId") String paymentMethodId);

    // Endpoints pour les catégories
    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("categories/popular")
    Call<List<Category>> getPopularCategories();

    @GET("categories/{categoryId}")
    Call<Category> getCategoryById(@Path("categoryId") String categoryId);

    // Endpoints pour l'authentification
    @POST("auth/register")
    Call<User> registerUser(@Body User user);

    @POST("auth/login")
    Call<User> loginUser(@Body User loginCredentials);

    // Endpoints pour les produits
    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/featured")
    Call<List<Product>> getFeaturedProducts();

    @GET("products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") String categoryId);

    @GET("products/search")
    Call<List<Product>> searchProducts(@Query("query") String query);

    @GET("products/{productId}")
    Call<Product> getProductDetails(@Path("productId") String productId);

    // Endpoints pour le panier
    @GET("cart/{userId}")
    Call<List<CartItem>> getCartItems(@Path("userId") String userId);

    @POST("cart")
    Call<CartItem> addToCart(@Body CartItem cartItem);

    @PUT("cart/{itemId}")
    Call<CartItem> updateCartItem(@Path("itemId") String itemId, @Body CartItem cartItem);

    @DELETE("cart/{itemId}")
    Call<Void> removeCartItem(@Path("itemId") String itemId);

    // Endpoints pour les commandes
    @POST("orders")
    Call<Order> placeOrder(@Body Order order);

    @GET("orders/{userId}")
    Call<List<Order>> getUserOrders(@Path("userId") String userId);

    @GET("orders/details/{orderId}")
    Call<Order> getOrderDetails(@Path("orderId") String orderId);

    // Endpoints pour le profil utilisateur
    @GET("users/{userId}")
    Call<User> getUserProfile(@Path("userId") String userId);

    @PUT("users/{userId}")
    Call<User> updateUserProfile(@Path("userId") String userId, @Body User user);
}
// utils/Constants.java
package com.example.ecommerce.utils;

public class Constants {
    // Préférences partagées
    public static final String PREF_NAME = "ecommerce_preferences";
    public static final String PREF_USER_LOGGED_IN = "user_logged_in";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_NAME = "user_name";

    // Intent extras
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_SEARCH_QUERY = "search_query";
    public static final String EXTRA_USER_ID = "user_id";

    // Codes de requête
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_REGISTER = 1002;
    public static final int REQUEST_CHECKOUT = 1003;
    public static final int REQUEST_PAYMENT = 1004;
    public static final int REQUEST_ADDRESS = 1005;

    // Codes de résultat
    public static final int RESULT_ADDRESS_ADDED = 2001;
    public static final int RESULT_PAYMENT_ADDED = 2002;

    // Catégories de notifications
    public static final String NOTIFICATION_CHANNEL_ORDERS = "orders";
    public static final String NOTIFICATION_CHANNEL_PROMOS = "promotions";
    public static final String NOTIFICATION_CHANNEL_GENERAL = "general";

    // États de commande
    public static final String ORDER_STATUS_PENDING = "pending";
    public static final String ORDER_STATUS_PROCESSING = "processing";
    public static final String ORDER_STATUS_SHIPPED = "shipped";
    public static final String ORDER_STATUS_DELIVERED = "delivered";
    public static final String ORDER_STATUS_CANCELLED = "cancelled";

    // Types de paiement
    public static final String PAYMENT_TYPE_CREDIT_CARD = "credit_card";
    public static final String PAYMENT_TYPE_PAYPAL = "paypal";
    public static final String PAYMENT_TYPE_GOOGLE_PAY = "google_pay";
    public static final String PAYMENT_TYPE_CASH_ON_DELIVERY = "cash_on_delivery";

    // Clés API et configurations
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_your_key_here";

    // Constantes pour la pagination
    public static final int ITEMS_PER_PAGE = 20;
}
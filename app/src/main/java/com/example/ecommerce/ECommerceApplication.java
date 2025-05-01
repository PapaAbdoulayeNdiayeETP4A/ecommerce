// ECommerceApplication.java
package com.example.ecommerce;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.ecommerce.utils.PreferenceManager;
import com.google.firebase.FirebaseApp;

public class ECommerceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialiser Firebase
        FirebaseApp.initializeApp(this);

        // Initialiser les préférences
        PreferenceManager.getInstance(this);

        // Créer le canal de notification pour Android Oreo et versions ultérieures
        createNotificationChannel();

        // Configurer Stripe
        configureStripe();
    }

    /**
     * Crée le canal de notification pour Android Oreo et versions ultérieures
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.notification_channel_id),
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.notification_channel_description));

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Configure Stripe pour les paiements
     */
    private void configureStripe() {
        // Initialiser Stripe avec la clé publique
        // Note: Cela serait normalement fait dans une vraie implémentation avec la bibliothèque Stripe
        // com.stripe.android.PaymentConfiguration.init(this, getString(R.string.stripe_publishable_key));
    }
}
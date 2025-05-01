package com.example.ecommerce.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Order;
import com.example.ecommerce.models.OrderItem;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.models.User;

@Database(entities = {User.class, Product.class, CartItem.class, Order.class, OrderItem.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Déclaration des DAO
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();
    public abstract AddressDao addressDao();
    public abstract NotificationDao notificationDao();
    public abstract PaymentMethodDao paymentMethodDao();
    public abstract WishlistDao wishlistDao();
    public abstract ReviewDao reviewDao();

    // Instance singleton
    private static volatile AppDatabase INSTANCE;

    // Méthode pour obtenir l'instance de la base de données
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "ecommerce_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
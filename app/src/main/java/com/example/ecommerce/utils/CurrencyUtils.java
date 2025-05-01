// utils/CurrencyUtils.java
package com.example.ecommerce.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyUtils {
    private static final NumberFormat DEFAULT_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    static {
        DEFAULT_CURRENCY_FORMAT.setCurrency(Currency.getInstance("USD"));
    }

    public static String formatCurrency(double amount) {
        return DEFAULT_CURRENCY_FORMAT.format(amount);
    }

    public static String formatCurrency(double amount, String currencyCode) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setCurrency(Currency.getInstance(currencyCode));
        return formatter.format(amount);
    }

    public static String formatCurrency(double amount, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(amount);
    }

    public static double calculateTax(double amount, double taxRate) {
        return amount * taxRate;
    }

    public static double calculateDiscount(double originalPrice, double discountPercentage) {
        return originalPrice * (discountPercentage / 100);
    }

    public static double calculatePriceAfterDiscount(double originalPrice, double discountPercentage) {
        return originalPrice - calculateDiscount(originalPrice, discountPercentage);
    }
}
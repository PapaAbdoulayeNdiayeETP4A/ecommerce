// utils/ValidationUtils.java
package com.example.ecommerce.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]+$");

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]{16}$");

    private static final Pattern CARD_CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$");

    private static final Pattern CARD_EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");

    private static final Pattern ZIPCODE_PATTERN = Pattern.compile("^[0-9]{5}(-[0-9]{4})?$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidZipCode(String zipCode) {
        return !TextUtils.isEmpty(zipCode) && ZIPCODE_PATTERN.matcher(zipCode).matches();
    }

    public static boolean isValidCardNumber(String cardNumber) {
        String cleanCardNumber = cardNumber.replaceAll("\\s", "");
        return !TextUtils.isEmpty(cleanCardNumber) && CARD_NUMBER_PATTERN.matcher(cleanCardNumber).matches() && luhnCheck(cleanCardNumber);
    }

    public static boolean isValidCVV(String cvv) {
        return !TextUtils.isEmpty(cvv) && CARD_CVV_PATTERN.matcher(cvv).matches();
    }

    public static boolean isValidCardExpiry(String expiry) {
        if (!TextUtils.isEmpty(expiry) && CARD_EXPIRY_PATTERN.matcher(expiry).matches()) {
            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000;

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentYear = calendar.get(java.util.Calendar.YEAR);
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;

            return (year > currentYear) || (year == currentYear && month >= currentMonth);
        }
        return false;
    }

    // Algorithme de Luhn pour vérifier la validité des numéros de carte
    private static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
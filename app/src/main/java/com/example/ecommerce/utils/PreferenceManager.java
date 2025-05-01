// utils/PreferenceManager.java
package com.example.ecommerce.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Classe utilitaire pour gérer les préférences partagées
 */
public class PreferenceManager {

    private static final String PREF_NAME = "ecommerce_preferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static PreferenceManager instance;
    private static final Gson gson = new Gson();

    private PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Enregistre une valeur booléenne
     */
    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Récupère une valeur booléenne
     */
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Récupère une valeur booléenne avec une valeur par défaut
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Enregistre une valeur entière
     */
    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Récupère une valeur entière
     */
    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Récupère une valeur entière avec une valeur par défaut
     */
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Enregistre une valeur longue
     */
    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Récupère une valeur longue
     */
    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }

    /**
     * Récupère une valeur longue avec une valeur par défaut
     */
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Enregistre une chaîne de caractères
     */
    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Récupère une chaîne de caractères
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    /**
     * Récupère une chaîne de caractères avec une valeur par défaut
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Enregistre un objet (sérialisé en JSON)
     */
    public <T> void setObject(String key, T object) {
        String json = gson.toJson(object);
        setString(key, json);
    }

    /**
     * Récupère un objet (désérialisé depuis JSON)
     */
    public <T> T getObject(String key, Class<T> classOfT) {
        String json = getString(key);
        if (json.isEmpty()) return null;
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Supprime une clé
     */
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    /**
     * Efface toutes les préférences
     */
    public void clear() {
        editor.clear();
        editor.apply();
    }

    /**
     * Vérifie si une clé existe
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }
}
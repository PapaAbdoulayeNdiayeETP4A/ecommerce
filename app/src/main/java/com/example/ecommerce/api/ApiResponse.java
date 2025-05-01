package com.example.ecommerce.api;

// Classe utilitaire pour encapsuler les réponses API
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    // Constructeur pour réponse réussie
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.error = null;
    }

    // Constructeur pour réponse échouée
    public ApiResponse(String error) {
        this.success = false;
        this.data = null;
        this.error = error;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
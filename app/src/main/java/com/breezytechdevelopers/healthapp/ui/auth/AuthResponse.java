package com.breezytechdevelopers.healthapp.ui.auth;

public interface AuthResponse {
    void onSuccess (AuthResponseModel response);
    void onFailure(String message);
}

package com.breezytechdevelopers.healthapp.ui.auth;

public interface AuthListener {
    void onAuthStarted();
    void onAuthSuccess(AuthResponseModel user);
    void onAuthFailure(String message);
}

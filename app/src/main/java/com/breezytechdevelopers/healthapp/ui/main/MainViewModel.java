package com.breezytechdevelopers.healthapp.ui.main;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.FirstAidTipBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.UserProfileBody;
import com.breezytechdevelopers.healthapp.ui.auth.AuthActivity;

public class MainViewModel extends AndroidViewModel {
    AppRepository appRepository;
    public LiveData<User> user;
    private MutableLiveData<Boolean> startPing;
    private String TAG = getClass().getSimpleName();
    private boolean hasShowLogin = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        startPing = new MutableLiveData<>();
        user = appRepository.getUser();

        // Check for User Logged in?
        getUser().observeForever(user -> {
            if (!hasShowLogin) {
                fetchFirstAidTips();
                hasShowLogin = true;
                if (user == null) {
                    showLogin(null);
                } else {
                    if (user.isAuthenticated()) {
                        appRepository.secretLogin(user);
                        fetchUserProfile(user.getToken());
                    } else {
                        showLogin(user);
                    }
                }
            }
        });
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void startPing(boolean b) {
        startPing.setValue(b);
    }

    public LiveData<Boolean> shouldStartPing() {
        return startPing;
    }

    private void fetchUserProfile(String token) {
        appRepository.fetchUserProfile(token, new ApiCallbacks.FetchUserProfile() {
            @Override public void onFetchStarted() {}
            @Override
            public void onFetchSuccess(UserProfileBody.Response userProfileResponse) {
                appRepository.saveUserProfile(userProfileResponse.getData());
            }

            @Override
            public void onFetchFailure(String message) {
                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "fetchUserProfile: onFetchFailure: " + message);
            }
        });
    }

    private void fetchFirstAidTips() {
        appRepository.fetchFirstAidTips(new ApiCallbacks.FetchFirstAidTips() {
            @Override public void onFetchStarted() {
                Log.i(TAG, "fetchFirstAidTips: onFetchStarted: ");
            }
            @Override
            public void onFetchSuccess(FirstAidTipBody.Response firstAidTipResponse) {
                appRepository.saveFirstAidTips(firstAidTipResponse.getFirstAidTips());
            }

            @Override
            public void onFetchFailure(String message) {
                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "fetchFirstAidTips: onFetchFailure: " + message);
            }
        });
    }

    private void showLogin(User user) {
        Intent myIntent = new Intent(getApplication(), AuthActivity.class);
        if (user != null && !user.isAuthenticated()) {
            myIntent.putExtra("user", user);
            myIntent.putExtra("motive", AppRepository.OTP);
        }
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(myIntent);
    }

    public void deleteTimers() {
        appRepository.deleteTimers();
    }
}
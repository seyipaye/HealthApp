package com.breezytechdevelopers.healthapp.ui.ping;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;

public class PingViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<User> userLiveData;
    public LiveData<Long> pingTimerMillisLeft;
    private LiveData<UserProfile> userProfileLiveData;

    public PingViewModel(Application application) {
        super(application);
        appRepository = new AppRepository(application);
        userLiveData = appRepository.getUser();
        userProfileLiveData = appRepository.getUserProfile();
        pingTimerMillisLeft = appRepository.getPingTimerMillisLeft();
    }

    public void startTimer(long seconds) {
        appRepository.startPingTimer(seconds);
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileLiveData;
    }

    public void sendPing(String token, String pingMessage, ApiCallbacks.Ping listener) {
        appRepository.sendPing(token, pingMessage, listener);
    }
}
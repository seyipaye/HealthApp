package com.breezytechdevelopers.healthapp.ui.ping;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.HealthApp;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.ui.firstAid.FirstAidViewModel;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PingViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<User> userLiveData;
    public LiveData<Long> pingTimerMillisLeft;
    private LiveData<UserProfile> userProfileLiveData;
    public MutableLiveData<PingViewModel.PingState> pingState;

    public PingViewModel(Application application) {
        super(application);
        appRepository = new AppRepository(application);
        userLiveData = appRepository.getUser();
        pingState = new MutableLiveData<>(PingState.NORMAL);
        userProfileLiveData = appRepository.getUserProfile();
        pingTimerMillisLeft = appRepository.getPingTimerMillisLeft();
        ((HealthApp) application).getSocketManager().getWebSocketState().observeForever(webSocketState -> {
            switch (webSocketState) {
                case CONNECTED:
                    pingState.postValue(PingState.ACTIVE_CHAT);
                    break;
                case DISCONNECTED:
                    pingState.postValue(PingState.NORMAL);
                    break;
                case STARTED:
                    pingState.postValue(PingState.GOT_RESPONSE);
                    break;
            }
            Log.i(TAG, "PingViewModel: " + webSocketState);
        });
    }

    public enum PingState {
        NORMAL,
        AWAITING_RESPONSE,
        GOT_RESPONSE,
        ACTIVE_CHAT
    }

    public MutableLiveData<PingState> getPingState() {
        return pingState;
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
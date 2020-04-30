package com.breezytechdevelopers.healthapp;

import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.breezytechdevelopers.healthapp.database.AppDatabase;
import com.breezytechdevelopers.healthapp.database.dao.FirstAidTipDao;
import com.breezytechdevelopers.healthapp.database.dao.TimerDao;
import com.breezytechdevelopers.healthapp.database.dao.UserDao;
import com.breezytechdevelopers.healthapp.database.dao.UserProfileDao;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;
import com.breezytechdevelopers.healthapp.database.entities.PingHistory;
import com.breezytechdevelopers.healthapp.database.entities.Timer;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingHistoryBody;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.AuthBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.FirstAidTipBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingBody;
import com.breezytechdevelopers.healthapp.network.SafeAPIRequest;
import com.breezytechdevelopers.healthapp.network.ApiBodies.UserProfileBody;
import com.breezytechdevelopers.healthapp.ui.auth.AuthResponse;
import com.breezytechdevelopers.healthapp.ui.auth.AuthResponseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository extends SafeAPIRequest {
    public static final String OTP = "otp";
    public static final String PING = "ping";
    public static final String CHANGEPASS = "changePassword";
    public static final String PINGHISTORY = "pingHistory";
    public static final String FORGOTPASS = "forgotPassword";
    public static final String PINGCHAT = "pingChat";
    private final FirstAidTipDao firstAidTipDao;
    private final TimerDao timerDao;
    private final AppDatabase appDatabase;
    private final UserProfileDao userProfileDao;
    private final Context context;
    private final UserDao userDao;
    private final PingHistoryDao pingHistoryDao;

    private final String TAG = "AppRepository";

    // <-------------------- DATABASE -------------------->
    public AppRepository(Application application) {
        appDatabase = AppDatabase.getDatabase(application);
        context = application.getApplicationContext();
        firstAidTipDao = appDatabase.firstAidTipDao();
        userProfileDao = appDatabase.userProfileDao();
        pingHistoryDao = appDatabase.pingHistoryDao();
        timerDao = appDatabase.timerDao();
        userDao = appDatabase.userDao();
    }

    public LiveData<List<FirstAidTip>> getTips() {
        return firstAidTipDao.getAllTips();
    }

    public LiveData<List<FirstAidTip>> searchTips(String query) {
        return appDatabase.firstAidTipDao().searchAllTips(query);
    }

    public LiveData<User> getUser() { return userDao.getUser(); }

    public void saveUser(User user) {
        if (user.getToken() != null) {
            fetchUserProfile(user.getToken(), null);
        }
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.userDao().insert(user));
    }

    public LiveData<UserProfile> getUserProfile() { return userProfileDao.getUserProfile(); }

    public void saveUserProfile(UserProfile userProfile) {
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.userProfileDao().insert(userProfile));
    }


    public void saveFirstAidTips(List<FirstAidTip> firstAidTips) {
        List<FirstAidTip> list = new ArrayList<>();
        for (FirstAidTip tip:
             firstAidTips) {
            list.add(new FirstAidTip());
        }
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.firstAidTipDao().insert(firstAidTips));
    }

    public LiveData<List<PingHistory>> getPingHistories() {
        return pingHistoryDao.getPingHistories();
    }

    public void savePingHistories(List<PingHistory> pingHistories) {
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.pingHistoryDao().insert(pingHistories));
    }


    public void logOut() {
        AppDatabase.dbWriteExecutor.execute(() -> {
            appDatabase.userDao().deleteUser();
            appDatabase.userProfileDao().deleteUserProfile();
            appDatabase.pingHistoryDao().deletePingHistory();
            appDatabase.timerDao().deleteTimers();
        });
    }

    // <-------------------- API -------------------->
    public void fetchUserProfile (String token, ApiCallbacks.FetchUserProfile listener ) {
        if (listener != null) {
            listener.onFetchStarted();
            apiRequest().fetchUserProfile("Bearer " + token).enqueue(new Callback<UserProfileBody.Response>() {
                @Override
                public void onResponse(Call<UserProfileBody.Response> call, Response<UserProfileBody.Response> response) {
                    if (response.isSuccessful()) {
                        listener.onFetchSuccess(response.body());
                        Log.i(TAG, "fetchUserProfile: " + response.body().getSuccess());
                    } else {
                        listener.onFetchFailure(getError( "fetchUserProfile", response.errorBody()));
                    }
                }

                @Override
                public void onFailure(Call<UserProfileBody.Response> call, Throwable t) {
                    listener.onFetchFailure(t.getLocalizedMessage());
                    Log.e(TAG, "fetchUserProfile: " + t.getMessage());
                }
            });
        } else {
                apiRequest().fetchUserProfile("Bearer " + token).enqueue(new Callback<UserProfileBody.Response>() {
                    @Override
                    public void onResponse(Call<UserProfileBody.Response> call, Response<UserProfileBody.Response> response) {
                        if (response.isSuccessful()) {
                            saveUserProfile(response.body().getData());
                            Log.i(TAG, "fetchUserProfile: " + response.body().getSuccess());
                        } else {
                            Toast.makeText(context, getError( "fetchUserProfile", response.errorBody()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileBody.Response> call, Throwable t) {
                        Log.i(TAG, "onResponse: " + t.getMessage());
                    }
                });
        }
    }

    public void fetchFirstAidTips (ApiCallbacks.FetchFirstAidTips listener) {
        listener.onFetchStarted();
        apiRequest().fetchFirstAidTips().enqueue(new Callback<FirstAidTipBody.Response>() {
            @Override
            public void onResponse(Call<FirstAidTipBody.Response> call, Response<FirstAidTipBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onFetchSuccess(response.body());
                    Log.i(TAG, "fetchFirstAidTips: " + response.body().getFirstAidTips());
                } else {
                    listener.onFetchFailure(getError( "fetchFirstAidTips", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<FirstAidTipBody.Response> call, Throwable t) {
                listener.onFetchFailure(t.getLocalizedMessage());
                Log.e(TAG, "fetchFirstAidTips: " + t.getMessage());
            }
        });
    }

    public void updateUserProfile(String token, UserProfileBody.Request request, ApiCallbacks.FetchUserProfile listener) {
        listener.onFetchStarted();
        apiRequest().updateUserProfile("Bearer " + token, request)
                .enqueue(new Callback<UserProfileBody.Response>() {
            @Override
            public void onResponse(Call<UserProfileBody.Response> call, Response<UserProfileBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onFetchSuccess(response.body());
                    Log.i(TAG, "updateUserProfile: " + response.body().getMessage());
                } else {
                    listener.onFetchFailure(getError( "updateUserProfile", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<UserProfileBody.Response> call, Throwable t) {
                listener.onFetchFailure(t.getLocalizedMessage());
                Log.e(TAG, "updateUserProfile: " + t.getMessage());
            }
        });
    }

    public void loginUser(User user, ApiCallbacks.Auth authResponse) {
        apiRequest().loginUser(user).enqueue(new Callback<AuthBody.Response>() {
            @Override
            public void onResponse(Call<AuthBody.Response> call, Response<AuthBody.Response> response) {
                if (response.isSuccessful()) {
                    authResponse.onAuthSuccess(response.body());
                } else {
                    authResponse.onAuthFailure(getError( "loginUser", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<AuthBody.Response> call, Throwable t) {
                authResponse.onAuthFailure(t.getLocalizedMessage());
                Log.e(TAG, "loginUser: " + t.getMessage());
            }
        });
    }

    public void forgotPassword (ChgPassBody.Request requestBody, ApiCallbacks.ChgPass callback) {
        apiRequest().resetPassword(requestBody).enqueue(new Callback<ChgPassBody.Response>() {
            @Override
            public void onResponse(Call<ChgPassBody.Response> call, Response<ChgPassBody.Response> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(getError("forgotPassword", response.errorBody()));
                }
            }
            @Override
            public void onFailure(Call<ChgPassBody.Response> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                Log.e(TAG, "forgotPassword: " + t.getMessage());
            }
        });
    }

    public void changePassword(ChgPassBody.Request requestBody, ApiCallbacks.ChgPass callback) {
        apiRequest().resetPassword(requestBody).enqueue(new Callback<ChgPassBody.Response>() {
            @Override
            public void onResponse(Call<ChgPassBody.Response> call, Response<ChgPassBody.Response> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(getError("changePassword", response.errorBody()));
                }
            }
            @Override
            public void onFailure(Call<ChgPassBody.Response> call, Throwable t) {
                callback.onFailure(t.getLocalizedMessage());
                Log.e(TAG, "changePassword: " + t.getMessage());
            }
        });
    }

    public void secretLogin(User user) {
        apiRequest().loginUser(user).enqueue(new Callback<AuthBody.Response>() {
            @Override
            public void onResponse(Call<AuthBody.Response> call, Response<AuthBody.Response> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: secretLogin : " + response.body().getMessage());
                    saveUser(user.setToken(response.body().getToken()).setAuthenticated(true));
                } else {
                    Toast.makeText(context, getError("secretLogin", response.errorBody()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthBody.Response> call, Throwable t) {
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "secretLogin : " + t.getMessage());
            }
        });
    }

    public void signupUser(User user, ApiCallbacks.Auth listener) {
        listener.onAuthStarted();
        apiRequest().signupUser(user).enqueue(new Callback<AuthBody.Response>() {
            @Override
            public void onResponse(Call<AuthBody.Response> call, Response<AuthBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onAuthSuccess(response.body());
                } else {
                    listener.onAuthFailure(getError("signupUser", response.errorBody()));
                }
            }
            @Override
            public void onFailure(Call<AuthBody.Response> call, Throwable t) {
                listener.onAuthFailure(t.getLocalizedMessage());
                Log.e(TAG, "signupUser: " + t.getMessage());
            }
        });
    }

    public void confirmOtp(String otp, User user, AuthResponse authResponse) {
        Call call;
        if (otp == null) {
            call = apiRequest().resendOtp(user.getAuthId());
        } else {
            call = apiRequest().confirmOtp(user.getAuthId(), otp);
        }

        call.enqueue(new Callback<AuthResponseModel>() {
            @Override
            public void onResponse(Call<AuthResponseModel> call, Response<AuthResponseModel> response) {
                if (response.isSuccessful()) {
                    if (otp != null) secretLogin(user);
                    authResponse.onSuccess(response.body());
                } else {
                    authResponse.onFailure(getError("confirmOtp", response.errorBody()));
                }
            }
            @Override
            public void onFailure(Call<AuthResponseModel> call, Throwable t) {
                authResponse.onFailure(t.getLocalizedMessage());
                Log.e(TAG, "confirmOtp: " + t.getMessage());
            }
        });
    }

    public void searchTipsInApi(String query, ApiCallbacks.SearchTipsInApi listener) {
        listener.onQueryStarted();
        apiRequest().searchTipsInApi(query).enqueue(new Callback<FirstAidTipBody.Response>() {
            @Override
            public void onResponse(Call<FirstAidTipBody.Response> call, Response<FirstAidTipBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onQuerySuccess(response.body());
                    Log.i(TAG, "searchTipsInApi: " + response.body());
                } else {
                    listener.onQueryFailure(getError( "searchTipsInApi", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<FirstAidTipBody.Response> call, Throwable t) {
                listener.onQueryFailure(t.getLocalizedMessage());
                Log.e(TAG, "searchTipsInApi: " + t.getMessage());
            }
        });
    }

    public void sendPing(String token, String pingMessage, ApiCallbacks.Ping listener) {
        listener.onPingStarted();
        Call call = apiRequest().sendPing("Bearer " + token, pingMessage);
        call.enqueue(new Callback<PingBody.Response>() {
            @Override
            public void onResponse(Call<PingBody.Response> call, Response<PingBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onPingSuccess(response.body());
                } else {
                    listener.onPingFailure(getError( "sendPing", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<PingBody.Response> call, Throwable t) {
                listener.onPingFailure(t.getLocalizedMessage());
                Log.e(TAG, "sendPing: " + t.getMessage());
            }
        });
    }

    public void fetchPingHistory(String token, ApiCallbacks.PingHistory listener) {
        Call call = apiRequest().fetchPingHistories ("Bearer " + token);
        call.enqueue(new Callback<PingHistoryBody.Response>() {
            @Override
            public void onResponse(Call<PingHistoryBody.Response> call, Response<PingHistoryBody.Response> response) {
                if (response.isSuccessful()) {
                    listener.onFetchSuccess(response.body());
                } else {
                    listener.onFetchFailure(getError( "fetchPingHistory", response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<PingHistoryBody.Response> call, Throwable t) {
                listener.onFetchFailure(t.getLocalizedMessage());
                Log.e(TAG, "fetchPingHistory: " + t.getMessage());
            }
        });
    }

    // <--------------------- Timers -------------------->
    public void startOtpTimer() {
        Timer otpTimer = new Timer(OTP, 0);
        storeTimerMillisLeft(otpTimer.setTimerMillisLeft(0));
        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                updateTimerMillisLeft(otpTimer.setTimerMillisLeft(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                updateTimerMillisLeft(otpTimer.setTimerMillisLeft(0));
            }
        }.start();
    }

    public LiveData<Long> getOtpTimerMillisLeft() {
            return timerDao.getOtpTimerMillisLeft(OTP);
    }

    public void startPingTimer(long seconds) {
        Timer pingTimer;
        pingTimer = new Timer(PING, 0);
        storeTimerMillisLeft(pingTimer.setTimerMillisLeft(0));
        new CountDownTimer((seconds * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
                updateTimerMillisLeft(pingTimer.setTimerMillisLeft(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                updateTimerMillisLeft(pingTimer.setTimerMillisLeft(0));
            }
        }.start();
    }

    public LiveData<Long> getPingTimerMillisLeft() {
        return timerDao.getPingTimerMillisLeft(PING);
    }

    public void deleteTimers() {
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.timerDao().deleteTimers());
    }

    private void storeTimerMillisLeft(Timer timer) {
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.timerDao().insert(timer));
    }

    private void updateTimerMillisLeft(Timer timer) {
        AppDatabase.dbWriteExecutor.execute(() -> appDatabase.timerDao().update(timer));
    }
}

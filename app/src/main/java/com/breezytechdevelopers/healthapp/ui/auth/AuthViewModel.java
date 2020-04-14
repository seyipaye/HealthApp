package com.breezytechdevelopers.healthapp.ui.auth;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.PingHistory;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingHistoryBody;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.AuthBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.utils.StartActivityModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class AuthViewModel extends AndroidViewModel implements AuthResponse {
    private static final int LOGIN_REQUEST_CODE = 101;
    private static final int SIGNUP_REQUEST_CODE = 202;
    private AppRepository appRepository;
    AuthListener authListener;
    LiveData<User> userLiveData;
    LiveData<List<PingHistory>> pingHistoryLiveData;
    private MutableLiveData<StartActivityModel> startActivityModel;
    private String TAG = getClass().getName();
    private Context context;
    private MutableLiveData<Boolean> hideKeybord;
    public LiveData<Long> otpTimerMillisLeft;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        startActivityModel = new MutableLiveData<>();
        hideKeybord = new MutableLiveData<>();
        userLiveData = appRepository.getUser();
        otpTimerMillisLeft = appRepository.getOtpTimerMillisLeft();
        pingHistoryLiveData = appRepository.getPingHistories();
        context = application.getApplicationContext();
    }

    MutableLiveData<StartActivityModel> startActivity() {
        return startActivityModel;
    }

    MutableLiveData<Boolean> shouldHideKeyboard() { return hideKeybord; }

    void confirmOtp(String otp, User user) {
        authListener.onAuthStarted();
        appRepository.confirmOtp(otp, user, this);
    }

    void resendOtp(User user, FrameLayout progressRing) {
        progressRing.setVisibility(View.VISIBLE);
        appRepository.confirmOtp(null, user, new AuthResponse() {
            @Override
            public void onSuccess(AuthResponseModel response) {
                progressRing.setVisibility(View.GONE);
                startOtpTimer();
                Toast.makeText(context, response.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                progressRing.setVisibility(View.GONE);
                Toast.makeText(context, message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startOtpTimer() {
        appRepository.startOtpTimer();
    }


    private AuthFragsListners.Signup signupListener;
    private AuthFragsListners.Login loginListener;

    void login(User user, AuthFragsListners.Login listener) {
        loginListener = listener;
        loginListener.onAuthStarted();
        if (user == null) {
            googleAuth(LOGIN_REQUEST_CODE);
        } else {
            appRepository.loginUser(user, new ApiCallbacks.Auth() {
                @Override public void onAuthStarted() { loginListener.onAuthStarted(); }
                @Override
                public void onAuthSuccess(AuthBody.Response authResponse) {
                    saveUser(user.setToken(authResponse.getToken()).setAuthenticated(true));
                    Log.i(TAG, "onAuthSuccess: " + authResponse + " : " + authResponse.getToken());
                    loginListener.onAuthSuccess(authResponse.getMessage());
                }
                @Override
                public void onAuthFailure(String message) {
                    loginListener.onAuthFailure(message);
                }
            });
        }
    }

    void signup(User user, AuthFragsListners.Signup listener) {
        signupListener = listener;
        signupListener.onStarted();
        if (user == null) {
            googleAuth(SIGNUP_REQUEST_CODE);
        } else {
            appRepository.signupUser(user, new ApiCallbacks.Auth() {
                @Override public void onAuthStarted() { signupListener.onStarted(); }
                @Override
                public void onAuthSuccess(AuthBody.Response authResponse) {
                    user.setAuthId(authResponse.getData().getId());
                    saveUser(user);
                    signupListener.onSuccess(user);
                }
                @Override
                public void onAuthFailure(String message) {
                    signupListener.onFailure(message);
                }
            });
        }
    }

    private void googleAuth(int requestCode) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            authenticateInAPI(account, requestCode);
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityModel.setValue(new StartActivityModel(signInIntent, requestCode));
    }

    void onAuthActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGIN_REQUEST_CODE || requestCode == SIGNUP_REQUEST_CODE) {

            // Try to get last sign in
            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                authenticateInAPI(account, requestCode);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode() + "\nMessage:" + e.getLocalizedMessage());
                if (requestCode == LOGIN_REQUEST_CODE)
                loginListener.onAuthFailure("signInResult:failed code=" + e.getStatusCode() + "\nMessage:" + e);
                else
                    signupListener.onFailure("signInResult:failed code=" + e.getStatusCode() + "\nMessage:" + e);
            }
        }
    }

    private void authenticateInAPI(GoogleSignInAccount account, int requestCode) {
        Log.i(TAG, "authenticateInAPI: " + account.getId());
        switch (requestCode) {
            case LOGIN_REQUEST_CODE:
                login(new User(account.getEmail(), account.getId()), loginListener);
                break;
            case SIGNUP_REQUEST_CODE:
                signup(new User(account.getEmail(), account.getId(), account.getGivenName(), account.getFamilyName()), signupListener);
                break;
        }
    }

    public void saveUser (User user) { appRepository.saveUser(user); }

    public void forgotPassword(ChgPassBody.Request requestBody, ApiCallbacks.ChgPass callBack) {
        appRepository.forgotPassword(requestBody, callBack);
    }

    public void sendOtpToEmail(ChgPassBody.Request requestBody, ApiCallbacks.ChgPass callBack) {
        appRepository.changePassword(requestBody, callBack);
    }

    public void changePassword(String otp, User user, ApiCallbacks.ChgPass callBack) {
        ChgPassBody.Request requestBody = new ChgPassBody.Request(user.getEmail(), otp,
                user.getPassword(), user.getPassword());
        appRepository.changePassword(requestBody, callBack);
    }

    @Override
    public void onSuccess(AuthResponseModel response) {
        authListener.onAuthSuccess(response);
    }

    @Override
    public void onFailure(String message) {
        authListener.onAuthFailure(message);
    }

    public void secretLogin(User user) {
        appRepository.secretLogin(user);
    }

    public LiveData<List<PingHistory>> getPingHistories() {
        return pingHistoryLiveData;
    }

    public void fetchPingHistories() {
        userLiveData.observeForever( user -> {
            if (user != null) {
                appRepository.fetchPingHistory(user.getToken(), new ApiCallbacks.PingHistory() {
                    @Override
                    public void onFetchSuccess(PingHistoryBody.Response historyResponse) {
                        if (historyResponse.getPingHistories() != null) {
                            Log.d(TAG, "onFetchSuccess: " + historyResponse.getPingHistories());
                            appRepository.savePingHistories(historyResponse.getPingHistories());
                        }
                    }

                    @Override
                    public void onFetchFailure(String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFetchFailure: " + message);
                    }
                });
            }
        });
    }
}
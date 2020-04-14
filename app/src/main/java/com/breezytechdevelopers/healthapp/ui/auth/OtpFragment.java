package com.breezytechdevelopers.healthapp.ui.auth;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthOtpBinding;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class OtpFragment extends DialogFragment implements View.OnClickListener, AuthListener {
    private User user;
    private AuthViewModel authViewModel;
    private FragmentAuthOtpBinding binding;
    private String TAG = getClass().getName();
    private String motive;
    private NavController navController;

    public OtpFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthOtpBinding.inflate(inflater, container, false);
        binding.backButton.setOnClickListener(this);
        binding.skipButton.setOnClickListener(this);
        binding.verifyButton.setOnClickListener(this);
        binding.resendButton.setOnClickListener(this);
        binding.skipButton.setOnClickListener(this);
        navController = Navigation.findNavController(requireActivity(), R.id.auth_host_fragment);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                    }
                });
        binding.otpEntry.setOnPinEnteredListener(str -> authViewModel.shouldHideKeyboard().setValue(true));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = OtpFragmentArgs.fromBundle(getArguments()).getUser();
        motive = OtpFragmentArgs.fromBundle(getArguments()).getMotive();
        binding.subTitle.append("\n" + user.getEmail());
    }

    @Override
    public void onStart() {
        super.onStart();
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.authListener = this;
        AtomicBoolean timeChanged = new AtomicBoolean(false);
        timeChanged.set(false);
        authViewModel.otpTimerMillisLeft.observe(getViewLifecycleOwner(), timerMillisLeft -> {
            if (timerMillisLeft != null) {
                if (timerMillisLeft != 0) {
                    if (timeChanged.get()) {
                        String hms = String.format(Locale.ENGLISH, "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timerMillisLeft) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(timerMillisLeft) % TimeUnit.MINUTES.toSeconds(1));
                        binding.expiryText.setText(hms);
                    }
                    timeChanged.set(true);
                }
            }
        });
        if (motive != null && motive.matches(AppRepository.FORGOTPASS)) {
            binding.skipButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skipButton:
                hideKeyboard();
                requireActivity().finish();
                break;
            case R.id.backButton:
                removeOtp(false);
                break;
            case R.id.verifyButton:
                if (inputIsValid())
                    switch (motive) {
                        case AppRepository.OTP:
                            authViewModel.confirmOtp(binding.otpEntry.getText().toString(), user);
                            break;
                        case AppRepository.CHANGEPASS:
                            changePassword();
                            break;
                        case AppRepository.FORGOTPASS:
                            forgotPassword();
                            break;
                    }
                break;
            case R.id.resendButton:
                authViewModel.resendOtp(user, binding.progressRing);
                break;
        }
    }

    private void forgotPassword() {
        binding.progressRing.setVisibility(View.VISIBLE);
        OtpFragmentDirections.ActionOtpFragmentToChgPassFragment action =
                OtpFragmentDirections.actionOtpFragmentToChgPassFragment(
                        user.setToken(binding.otpEntry.getText().toString()),
                        AppRepository.FORGOTPASS);
        NavHostFragment.findNavController(this).navigate(action);
        binding.progressRing.setVisibility(View.GONE);
    }

    private void changePassword() {
        binding.progressRing.setVisibility(View.VISIBLE);
        authViewModel.changePassword(binding.otpEntry.getText().toString(), user, new ApiCallbacks.ChgPass() {
            @Override public void onStarted() {}
            @Override
            public void onSuccess(ChgPassBody.Response chgPassResponse) {
                binding.progressRing.setVisibility(View.GONE);
                authViewModel.saveUser(user);
                authViewModel.secretLogin(user);
                Toast.makeText(getActivity(), chgPassResponse.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onAuthSuccess: " + chgPassResponse.getMessage());
                removeOtp(true);
            }
            @Override
            public void onFailure(String message) {
                binding.progressRing.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
                Log.i(TAG, "onAuthFailure: " + message);
            }
        });
    }

    private void removeOtp(boolean totally) {
        hideKeyboard();
        if (totally)
            requireActivity().finish();
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onAuthStarted() {
        binding.progressRing.setVisibility(View.VISIBLE);
        Log.i(TAG, "onAuthStarted: STARTED");
    }

    @Override
    public void onAuthSuccess(AuthResponseModel user) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(getActivity(), user.getMessage(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "onAuthSuccess: " + user.getMessage());
        removeOtp(true);
    }

    @Override
    public void onAuthFailure(String message) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        Log.i(TAG, "onAuthFailure: " + message);
    }

    private void hideKeyboard() {
        hideKeyboardFrom(binding.otpEntry);
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean inputIsValid() {
        boolean valid = true;

        String passwordT = binding.otpEntry.getText().toString();
        if (TextUtils.isEmpty(passwordT)) {
            valid = false;
            Toast.makeText(getActivity(), "Enter OTP", Toast.LENGTH_SHORT).show();
            binding.otpEntry.requestFocus();
        } else if (passwordT.length() < 5){
            valid = false;
            binding.otpEntry.setError("Pin must be 5 digits");
            binding.otpEntry.requestFocus();
        }

        Log.d(TAG, "validated: " + valid);
        return valid;
    }
}

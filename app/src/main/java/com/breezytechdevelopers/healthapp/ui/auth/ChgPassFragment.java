package com.breezytechdevelopers.healthapp.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthChgPassBinding;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChgPassFragment extends Fragment implements View.OnClickListener, ApiCallbacks.ChgPass {

    private FragmentAuthChgPassBinding binding;
    private User user;
    private AuthViewModel authViewModel;
    private String motive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentAuthChgPassBinding.inflate(inflater, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        backPressed();
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        if (getArguments() != null) {
            user = OtpFragmentArgs.fromBundle(getArguments()).getUser();
            motive = OtpFragmentArgs.fromBundle(getArguments()).getMotive();
        }
        binding.backButton.setOnClickListener(this);
        binding.chgPassButton.setOnClickListener(this);
        binding.retypePasswordEntry.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboardFrom(v);
                binding.progressRing.setVisibility(View.VISIBLE);
                switch (motive) {
                    case AppRepository.CHANGEPASS:
                        authViewModel.sendOtpToEmail(new ChgPassBody.Request(user.getEmail()), this);
                        break;
                    case AppRepository.FORGOTPASS:
                        forgotPassword(v.getText().toString());
                        break;
                }
                handled = true;
            }
            return handled;
        });
        if (motive != null && motive.equals(AppRepository.FORGOTPASS)) {
            binding.currentPasswordLayout.setVisibility(View.GONE);
        }
    }

    private void forgotPassword(String password) {
        authViewModel.forgotPassword(new ChgPassBody.Request(user.getEmail(), user.getToken(), password, password), new ApiCallbacks.ChgPass() {
            @Override
            public void onStarted() { binding.progressRing.setVisibility(View.VISIBLE); }
            @Override
            public void onSuccess(ChgPassBody.Response chgPassResponse) {
                binding.progressRing.setVisibility(View.GONE);
                authViewModel.saveUser(user.setPassword(password));
                authViewModel.secretLogin(user.setPassword(password));
                removeChgPass(true);
            }

            @Override
            public void onFailure(String message) {
                binding.progressRing.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                if (message.contains("OTP code is invalid or expired")) {
                    backPressed();
                }
            }
        });
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                backPressed();
                break;
            case R.id.chgPassButton:
                if (inputIsValid()) {
                    hideKeyboard();
                    binding.retypePasswordEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
        }
    }

    private void backPressed() {
        if (motive!= null && motive.equals(AppRepository.FORGOTPASS))
        removeChgPass(false);
        else
            removeChgPass(true);
    }

    private boolean inputIsValid() {
        boolean valid = true;

        if (motive == null || !motive.equals(AppRepository.FORGOTPASS)) {
            binding.currentPasswordLayout.setVisibility(View.GONE);

            String passwordT = binding.currentPasswordEntry.getText().toString();
            if (!TextUtils.isEmpty(passwordT)) {
                if (passwordT.matches("null") || passwordT.matches("nil")) {
                    valid = false;
                    binding.currentPasswordEntry.setError("Please note that password must not contain programming syntax");
                    binding.currentPasswordEntry.requestFocus();
                } else {
                    binding.currentPasswordEntry.setError(null);
                }
            } else if (passwordT.length() < 6) {
                valid = false;
                binding.currentPasswordEntry.setError("You must have a minimum of 6 characters in your password");
            }
        }

        String passwordT2 = binding.newPasswordEntry.getText().toString();
        if (!TextUtils.isEmpty(passwordT2)) {
            if (passwordT2.matches("null") || passwordT2.matches("nil")) {
                valid = false;
                binding.newPasswordEntry.setError("Please note that password must not contain programming syntax");
                binding.newPasswordEntry.requestFocus();
            } else {
                binding.newPasswordEntry.setError(null);
            }
        } else if (passwordT2.length() < 6){
            valid = false;
            binding.newPasswordEntry.setError("You must have a minimum of 6 characters in your password");
        }

        String passwordT3 = binding.retypePasswordEntry.getText().toString();
        if (!TextUtils.isEmpty(passwordT3)) {
            if (!passwordT3.matches(passwordT2)) {
                valid = false;
                binding.retypePasswordEntry.setError("This must be the same as the new Password");
                binding.retypePasswordEntry.requestFocus();
            } else {
                binding.retypePasswordEntry.setError(null);
            }
        } else if (passwordT3.length() < 6){
            valid = false;
            binding.retypePasswordEntry.setError("You must have a minimum of 6 characters in your password");
        }



        Log.d(TAG, "validated: " + valid);
        return valid;
    }

    private void removeChgPass(boolean totally) {
        hideKeyboard();
        if (totally)
        requireActivity().finish();
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    private void hideKeyboard() {
        hideKeyboardFrom(binding.currentPasswordEntry);
        hideKeyboardFrom(binding.newPasswordEntry);
        hideKeyboardFrom(binding.retypePasswordEntry);
    }

    private void showOtp(String newPassword) {
        ChgPassFragmentDirections.ActionChgPassFragmentToOtpFragment action =
                ChgPassFragmentDirections.actionChgPassFragmentToOtpFragment(
                        user.setPassword(newPassword), AppRepository.CHANGEPASS);
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onStarted() {
        binding.progressRing.setVisibility(View.VISIBLE);
    }
    @Override
    public void onSuccess(ChgPassBody.Response chgPassResponse) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), chgPassResponse.getMessage(), Toast.LENGTH_SHORT).show();
        authViewModel.startOtpTimer();
        showOtp(binding.newPasswordEntry.getText().toString());
    }
    @Override
    public void onFailure(String message) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.breezytechdevelopers.healthapp.ui.auth;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthSignupBinding;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;

public class SignupFragment extends DialogFragment implements View.OnClickListener, AuthFragsListners.Signup {
    private  AuthViewModel authViewModel;
    private FragmentAuthSignupBinding binding;
    private String TAG = getClass().getName();
    private NavController navController;
    private Boolean signupWithGoogle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthSignupBinding.inflate(inflater, container, false);
        binding.skipButton.setOnClickListener(this);
        binding.registerButton.setOnClickListener(this);
        binding.loginText.setOnClickListener(this);
        binding.googleSignupButton.setOnClickListener(this);
        navController = Navigation.findNavController(requireActivity(), R.id.auth_host_fragment);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        signupWithGoogle = SignupFragmentArgs.fromBundle(getArguments()).getSignUpWithGoogle();
        if (signupWithGoogle) {
            hideKeyboard();
            authViewModel.signup(null, this);
        }
        binding.passwordEntry.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (inputIsValid()) {
                    hideKeyboard();
                    try {
                        User user = new User(
                                binding.emailEntry.getText().toString(),
                                binding.passwordEntry.getText().toString(),
                                binding.fNameEntry.getText().toString(),
                                binding.lNameEntry.getText().toString());
                        authViewModel.signup(user, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handled = true;
            }
            return handled;
        });
    }

    private void removeSignup(boolean totally) {
        hideKeyboard();
        if (totally)
            requireActivity().finish();
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    private void showOtp(User user) {
        hideKeyboard();
        authViewModel.startOtpTimer();
        SignupFragmentDirections.ActionSignupFragmentToOtpFragment action =
                SignupFragmentDirections.actionSignupFragmentToOtpFragment(user, AppRepository.OTP);

        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignupButton:
                hideKeyboard();
                authViewModel.signup(null, this);
                break;
            case R.id.registerButton:
                //showOtp(null,null);
                binding.passwordEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            case R.id.loginText:
                showLogin();
                break;
            case R.id.skipButton:
                removeSignup(true);
                break;
        }
    }

    private void showLogin() {
        hideKeyboard();
        NavHostFragment.findNavController(this).navigate(R.id.action_signupFragment_to_loginFragment);
    }

    private void hideKeyboard() {
        hideKeyboardFrom(binding.fNameEntry);
        hideKeyboardFrom(binding.lNameEntry);
        hideKeyboardFrom(binding.emailEntry);
        hideKeyboardFrom(binding.passwordEntry);
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean inputIsValid() {
        boolean valid = true;
        Log.d(TAG, "validating...:");

        String firstNameT = binding.fNameEntry.getText().toString();
        if (!TextUtils.isEmpty(firstNameT)) {
            if (firstNameT.matches("null")) {
                valid = false;
                binding.fNameEntry.setError("Please enter a valid name");
            } else {
                binding.fNameEntry.setError(null);
            }
        } else {
            valid = false;
            binding.fNameEntry.setError("Required");
        }

        String lastNameT= binding.lNameEntry.getText().toString();
        if (!TextUtils.isEmpty(lastNameT)) {
            if (firstNameT.matches("null")) {
                valid = false;
                binding.lNameEntry.setError("Please enter a valid name");
            } else {
                binding.lNameEntry.setError(null);
            }
        } else {
            valid = false;
            binding.lNameEntry.setError("Required");
        }

        String emailT = binding.emailEntry.getText().toString();
        if (!TextUtils.isEmpty(emailT)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailT).matches()) {
                valid = false;
                binding.emailEntry.setError("Please enter a valid email address");
            } else {
                binding.emailEntry.setError(null);
            }
        } else {
            valid = false;
            binding.emailEntry.setError("Required");
        }

        String passwordT = binding.passwordEntry.getText().toString();
        if (!TextUtils.isEmpty(passwordT)) {
            if (passwordT.matches("null") || passwordT.matches("nil")) {
                valid = false;
                binding.passwordEntry.setError("Please note that password must not contain programming syntax");
                binding.passwordEntry.requestFocus();
            } else {
                binding.passwordEntry.setError(null);
            }
        } else if (passwordT.length() < 6){
            valid = false;
            binding.passwordEntry.setError("You must have a minimum of 6 characters in your password");
        }

        Log.d(TAG, "validated: " + valid);
        return valid;
    }

    @Override
    public void onStarted() {
        binding.progressRing.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(User user) {
        Log.i(TAG, "onAuthSuccess: " + user.getAuthId());
        binding.progressRing.setVisibility(View.GONE);
        showOtp(user);
    }

    @Override
    public void onFailure(String message) {
        Log.i(TAG, "onAuthFailure: " + message);
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        authViewModel.forgetSelectedGoogleAccount();
    }
}

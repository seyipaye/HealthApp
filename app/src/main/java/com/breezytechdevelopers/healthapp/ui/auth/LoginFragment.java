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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthLoginBinding;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;

public class LoginFragment extends DialogFragment implements View.OnClickListener, AuthFragsListners.Login {
    private AuthViewModel authViewModel;
    private FragmentAuthLoginBinding binding;
    private String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAuthLoginBinding.inflate(inflater, container, false);
        binding.cancelButton.setOnClickListener(this);
        binding.loginButton.setOnClickListener(this);
        binding.forgotPassButton.setOnClickListener(this);
        binding.signupText.setOnClickListener(this);
        binding.googleLoginButton.setOnClickListener(this);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.auth_host_fragment);
        Log.i(TAG, "onCreate: " + navController.toString());
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
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        binding.passwordEntry.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (inputIsValid()) {
                    hideKeyboard();
                    authViewModel.login(new User(binding.emailEntry.getText().toString(), binding.passwordEntry.getText().toString()), this);
                }
                handled = true;
            }
            return handled;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                removeLogin(false);
                break;
            case R.id.loginButton:
                binding.passwordEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            case R.id.forgotPassButton:
                showForgotPass(binding.emailEntry.getText().toString());
                break;
            case R.id.signupText:
                showSignup();
                break;
            case R.id.googleLoginButton:
                hideKeyboard();
                authViewModel.login(null, this);
                break;
        }
    }

    private void showSignup() {
        LoginFragmentDirections.ActionLoginFragmentToSignupFragment action =
                LoginFragmentDirections.actionLoginFragmentToSignupFragment(false);
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void showForgotPass(String email) {
        hideKeyboard();
        LoginFragmentDirections.ActionLoginFragmentToForgotPassFragment action = LoginFragmentDirections.actionLoginFragmentToForgotPassFragment(email);

        NavHostFragment.findNavController(this).navigate(action);
    }

    private void hideKeyboard() {
        hideKeyboardFrom(binding.emailEntry);
        hideKeyboardFrom(binding.passwordEntry);
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void removeLogin(boolean totally) {
        hideKeyboard();
        if (totally)
            requireActivity().finish();
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    private boolean inputIsValid() {
        boolean valid = true;

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
    public void onAuthStarted() {
        binding.progressRing.setVisibility(View.VISIBLE);
        Log.i(TAG, "onAuthStarted: STARTED");
    }

    @Override
    public void onAuthSuccess(String message) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
        removeLogin(true);
    }

    @Override
    public void onAuthFailure(String message) {
        authViewModel.forgetSelectedGoogleAccount();
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        Log.i(TAG, "onAuthFailure: " + message);
    }
}

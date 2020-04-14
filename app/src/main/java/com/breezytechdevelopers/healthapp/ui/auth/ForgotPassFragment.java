package com.breezytechdevelopers.healthapp.ui.auth;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthForgotPassBinding;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ForgotPassFragment extends Fragment implements View.OnClickListener, ApiCallbacks.ChgPass {
    private FragmentAuthForgotPassBinding binding;
    private String email;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAuthForgotPassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            email = ForgotPassFragmentArgs.fromBundle(getArguments()).getEmail();
        }
        binding.emailEntry.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboardFrom(v);
                binding.progressRing.setVisibility(View.VISIBLE);
                authViewModel.sendOtpToEmail(new ChgPassBody.Request(v.getText().toString()), this);
                handled = true;
            }
            return handled;
        });
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        binding.emailEntry.setText(email);
        binding.backButton.setOnClickListener(this);
        binding.submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                removeForgotPass();
                break;
            case R.id.submitBtn:
                if (inputIsValid()) {
                    binding.progressRing.setVisibility(View.VISIBLE);
                    binding.emailEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
                break;
        }
    }

    private void showOtp(User tempUser) {
        Bundle bundle = new Bundle();
        bundle.putString("email", tempUser.getEmail());
        setArguments(bundle);
        ForgotPassFragmentDirections.ActionForgotPassFragmentToOtpFragment action =
                ForgotPassFragmentDirections.actionForgotPassFragmentToOtpFragment(
                        tempUser, AppRepository.FORGOTPASS);
        NavHostFragment.findNavController(ForgotPassFragment.this)
                .navigate(action);
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
        showOtp(new User(binding.emailEntry.getText().toString()));
    }

    @Override
    public void onFailure(String message) {
        binding.progressRing.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        if (message.contains("OTP already generated")) {
            showOtp(new User(binding.emailEntry.getText().toString()));
        }
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

        Log.d(TAG, "validated: " + valid);
        return valid;
    }

    private void removeForgotPass() {
        hideKeyboard();
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void hideKeyboard() {
        hideKeyboardFrom(binding.emailEntry);
    }
}

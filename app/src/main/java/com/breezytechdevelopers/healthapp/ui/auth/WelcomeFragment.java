package com.breezytechdevelopers.healthapp.ui.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.databinding.FragmentAuthWelcomeBinding;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private FragmentAuthWelcomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAuthWelcomeBinding.inflate(inflater, container, false);
        binding.registerButton.setOnClickListener(this);
        binding.loginText.setOnClickListener(this);
        binding.googleSignupButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                showSignup(false);
                break;
            case R.id.googleSignupButton:
                showSignup(true);
                break;
            case R.id.loginText:
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_welcomeFragment_to_loginFragment);
                break;
        }
    }

    private void showSignup(boolean shouldSignupWithGoogle) {
        WelcomeFragmentDirections.ActionWelcomeFragmentToSignupFragment action =
                WelcomeFragmentDirections.actionWelcomeFragmentToSignupFragment(shouldSignupWithGoogle);

        NavHostFragment.findNavController(this).navigate(action);
    }
}

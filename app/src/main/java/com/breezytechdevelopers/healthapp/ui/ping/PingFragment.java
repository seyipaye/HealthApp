package com.breezytechdevelopers.healthapp.ui.ping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingBody;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingBinding;
import com.breezytechdevelopers.healthapp.ui.auth.AuthActivity;
import com.breezytechdevelopers.healthapp.ui.fullscreen.FullscreenActivity;
import com.breezytechdevelopers.healthapp.utils.FragmentVisibleInterface;

public class PingFragment extends Fragment implements View.OnClickListener {

    private PingViewModel pingViewModel;
    private FragmentPingBinding binding;
    private String TAG = "PingViewModel";
    private User user;
    private UserProfile userProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPingBinding.inflate(inflater, container, false);
        binding.sendPingBtn.setOnClickListener(this);
        binding.callAmbulanceBtn.setOnClickListener(this);
        pingViewModel = new ViewModelProvider(this).get(PingViewModel.class);
        subscribeUI();
        return binding.getRoot();
    }

    private void subscribeUI() {
        Log.i(TAG, "subscribeUI: ");
        pingViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            this.user = user;
        });
        pingViewModel.getUserProfile().observe(getViewLifecycleOwner(), userProfile -> {
            this.userProfile = userProfile;
        });
        binding.messageEntry.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        binding.messageEntry.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboardFrom(requireContext(), v);
                sendPing();
                handled = true;
            }
            return handled;
        });
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentVisibleInterface fragVisInterface = (FragmentVisibleInterface) getActivity();
        fragVisInterface.onFragmentVisible(R.layout.fragment_ping);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendPingBtn:
                binding.messageEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                break;
            case R.id.callAmbulanceBtn:
                Toast.makeText(getActivity(), "Calling Ambulance", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showChat(String pingID) {
        Intent myIntent = new Intent(requireActivity(), FullscreenActivity.class);
        if (user != null && user.isAuthenticated()) {
            myIntent.putExtra("pingID", user);
            myIntent.putExtra("motive", AppRepository.PINGCHAT);
        }
        requireActivity().startActivity(myIntent);
    }

    private void sendPing() {
        if (userProfileComplete()) {
            if (!TextUtils.isEmpty(binding.messageEntry.getText())) {
                pingViewModel.sendPing(user.getToken(),
                        binding.messageEntry.getText().toString(),
                        new ApiCallbacks.Ping() {
                    @Override
                    public void onPingStarted() {
                        binding.pingEntryLayout.setVisibility(View.GONE);
                        binding.awaitingResponseTV.setVisibility(View.VISIBLE);
                        binding.sendPingBtn.setText("Pinging...");
                        binding.cancelPingBtn.setVisibility(View.VISIBLE);
                        binding.sendPingBtn.setClickable(false);
                    }
                    @Override
                    public void onPingSuccess(PingBody.Response pingResponse) {
                        pingViewModel.startTimer(180);
                        binding.sendPingBtn.setText("Sent Ping");
                        binding.cancelPingBtn.setVisibility(View.VISIBLE);
                        binding.sendPingBtn.setClickable(false);
                        showChat(pingResponse.getData().getId());
                        Toast.makeText(getContext(), pingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPingFailure(String message) {
                        binding.pingEntryLayout.setVisibility(View.VISIBLE);
                        binding.awaitingResponseTV.setVisibility(View.GONE);
                        binding.sendPingBtn.setText("Send Ping");
                        binding.cancelPingBtn.setVisibility(View.GONE);
                        binding.sendPingBtn.setClickable(true);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Type a message, Please", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean userProfileComplete() {
        boolean valid = true;
        Log.d(TAG, "validating ...:");

        if (user != null) {
            if (user.getToken() != null) {
                if (userProfile != null) {
                    if (!TextUtils.isEmpty(userProfile.getClinic_number())) {
                        if (!TextUtils.isEmpty(userProfile.getDepartment())) {
                            if (!TextUtils.isEmpty(userProfile.getMatric_number())) {
                                if (!TextUtils.isEmpty(userProfile.getMobile_number())) {

                                } else {
                                    valid = false;
                                }
                            } else {
                                valid = false;
                            }
                        } else {
                            valid = false;
                        }
                    } else {
                        valid = false;
                    }
                    if (!valid) {
                        Toast.makeText(getContext(), "You must complete profile to send ping", Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(this).navigate(R.id.navigation_profile);
                    }
                } else {
                    valid = false;
                    Toast.makeText(getContext(), "User not logged in. Try again later", Toast.LENGTH_SHORT).show();
                }
            } else {
                valid = false;
                Toast.makeText(getContext(), "You must validate account to send a ping", Toast.LENGTH_SHORT).show();
            }
        } else {
            valid = false;
            Toast.makeText(getContext(), "You must register or login to send a ping", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }
    
}
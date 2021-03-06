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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.HealthApp;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingBody;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingBinding;
import com.breezytechdevelopers.healthapp.ui.fullscreen.FullscreenActivity;
import com.breezytechdevelopers.healthapp.utils.FragmentVisibleInterface;

import javax.sql.StatementEvent;

public class PingFragment extends Fragment implements View.OnClickListener {

    private PingViewModel pingViewModel;
    private FragmentPingBinding binding;
    private String TAG = "PingViewModel";
    private User user;
    private UserProfile userProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPingBinding.inflate(inflater, container, false);
        binding.solidPingBtn.setOnClickListener(this);
        binding.cancelPingBtn.setOnClickListener(this);
        pingViewModel = new ViewModelProvider(this).get(PingViewModel.class);
        subscribeUI();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
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
        pingViewModel.getPingState().observe(getViewLifecycleOwner(), pingState -> {
            switch (pingState) {
                case NORMAL:
                    binding.pingEntryLayout.setVisibility(View.VISIBLE);
                    binding.topLabel.setVisibility(View.GONE);
                    binding.solidPingBtn.setText("Send Ping");
                    binding.solidPingBtn.setClickable(true);
                    binding.cancelPingBtn.setVisibility(View.GONE);
                    break;
                case AWAITING_RESPONSE:
                    binding.pingEntryLayout.setVisibility(View.GONE);
                    binding.topLabel.setVisibility(View.VISIBLE);
                    binding.topLabel.setText("Awaiting a response...");
                    binding.solidPingBtn.setText("Pinging...");
                    binding.solidPingBtn.setClickable(false);
                    binding.cancelPingBtn.setVisibility(View.VISIBLE);
                    break;
                case GOT_RESPONSE:
                    binding.pingEntryLayout.setVisibility(View.GONE);
                    binding.topLabel.setVisibility(View.VISIBLE);
                    binding.topLabel.setText("Initializing...");
                    binding.solidPingBtn.setText("Start chat");
                    binding.solidPingBtn.setClickable(true);
                    binding.cancelPingBtn.setVisibility(View.VISIBLE);
                    break;
                case ACTIVE_CHAT:
                    binding.pingEntryLayout.setVisibility(View.GONE);
                    binding.topLabel.setVisibility(View.VISIBLE);
                    binding.topLabel.setText("Active chat");
                    binding.solidPingBtn.setText("Resume chat");
                    binding.solidPingBtn.setClickable(true);
                    binding.cancelPingBtn.setVisibility(View.VISIBLE);
                    break;
            }
            Log.i(TAG, "subscribeUI: " + pingState);
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
            case R.id.solidPingBtn:
                if (((Button) v).getText().toString().contains("Send Ping")) {
                    binding.messageEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
                } else {
                    showChat("i", "m");
                }
                break;
            case R.id.cancelPingBtn:
                //pingViewModel.getPingState().postValue(PingViewModel.PingState.NORMAL);
                ((HealthApp) requireActivity().getApplication()).getSocketManager().destroy();
                break;
        }
    }

    private void showChat(String pingID, String message) {
        Intent myIntent = new Intent(requireActivity(), FullscreenActivity.class);
        if (user != null && user.isAuthenticated()) {
            myIntent.putExtra("pingID", pingID);
            myIntent.putExtra("token", user.getToken());
            myIntent.putExtra("initialMessage", message);
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
                        pingViewModel.getPingState().postValue(PingViewModel.PingState.AWAITING_RESPONSE);
                    }
                    @Override
                    public void onPingSuccess(PingBody.Response pingResponse) {
                        showChat(pingResponse.getData().getId(), pingResponse.getData().getMessage());
                        Toast.makeText(getContext(), pingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPingFailure(String message) {
                        pingViewModel.getPingState().postValue(PingViewModel.PingState.NORMAL);
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
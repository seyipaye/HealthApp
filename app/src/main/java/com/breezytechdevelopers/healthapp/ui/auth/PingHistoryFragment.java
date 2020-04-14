package com.breezytechdevelopers.healthapp.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingHistoryBinding;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingHistoryBody;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;

public class PingHistoryFragment extends Fragment implements View.OnClickListener {

    private FragmentPingHistoryBinding binding;
    private AuthViewModel authViewModel;
    private String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentPingHistoryBinding.inflate(inflater, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        removeChgPass();
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        binding.backButton.setOnClickListener(this);
        PingHistoryAdapter pingHistoryAdapter = new PingHistoryAdapter();
        binding.pingHistoryRV.setAdapter(pingHistoryAdapter);
        authViewModel.fetchPingHistories();
        authViewModel.getPingHistories().observe(getViewLifecycleOwner(), pingHistories -> {
            pingHistoryAdapter.setPingHistoryList(pingHistories);
            Log.d(TAG, "onViewCreated: " + pingHistories);;
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            removeChgPass();
        }
    }

    private void removeChgPass() {
        requireActivity().finish();
    }

}

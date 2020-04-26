package com.breezytechdevelopers.healthapp.ui.fullscreen;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;
import com.breezytechdevelopers.healthapp.database.entities.Message;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingChatBinding;
import com.breezytechdevelopers.healthapp.ui.firstAid.FirstAidRVAdapter;
import com.breezytechdevelopers.healthapp.ui.profile.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class PingChatFragment extends Fragment {

    private PingChatViewModel pingChatViewModel;
    private FragmentPingChatBinding binding;
    PingChatListViewAdapter pingChatListViewAdapter;


    public static PingChatFragment newInstance() {
        return new PingChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPingChatBinding.inflate(inflater);
        pingChatViewModel = new ViewModelProvider(this).get(PingChatViewModel.class);
        pingChatListViewAdapter = new PingChatListViewAdapter(requireContext(), pingChatViewModel.getMutableAvatar());
        binding.pingChatListView.setAdapter(pingChatListViewAdapter);

        // Start
        List<FirstAidTip> firstAidTips = new ArrayList<>();
        boolean change = true;
        for (int i = 0; i < 10; i++) {
            if (change) {
                pingChatListViewAdapter.add(new Message("", change));
                change = false;
            } else {
                pingChatListViewAdapter.add(new Message("", change));
                change = true;
            }
        }
        return binding.getRoot();
    }
}

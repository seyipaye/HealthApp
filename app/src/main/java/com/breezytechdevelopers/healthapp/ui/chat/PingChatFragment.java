package com.breezytechdevelopers.healthapp.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.breezytechdevelopers.healthapp.HealthApp;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.Message;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingChatBinding;
import com.breezytechdevelopers.healthapp.utils.SocketManager;
import com.breezytechdevelopers.healthapp.utils.Utils;
import com.neovisionaries.ws.client.WebSocketException;

public class PingChatFragment extends Fragment implements View.OnClickListener {

    private PingChatViewModel pingChatViewModel;
    private FragmentPingChatBinding binding;
    PingChatRVAdapter pingChatRVAdapter;
    SocketManager socketManager;
    String TAG = getClass().getSimpleName();

    public static PingChatFragment newInstance() {
        return new PingChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPingChatBinding.inflate(inflater);
        pingChatViewModel = new ViewModelProvider(this).get(PingChatViewModel.class);
        pingChatRVAdapter = new PingChatRVAdapter(requireContext(), pingChatViewModel.getMutableAvatar());
        binding.pingChatRV.setAdapter(pingChatRVAdapter);

       /* // Start
        boolean change = true;
        for (int i = 0; i < 10; i++) {
            if (change) {
                pingChatRVAdapter.add(new Message("sdkunalsknalksaxasxsa", change));
                change = false;
            } else {
                pingChatRVAdapter.add(new Message("adcasdcasascacas", change));
                change = true;
            }
        }
*/
        binding.backButton.setOnClickListener(this);
        binding.sendButton.setOnClickListener(this);
        socketManager = ((HealthApp) requireActivity().getApplication()).getSocketManager();
        /*socketManager.addMessage((new Message("Hi, this is an echo web socket, " +
                "It say's exactly what you say, try it out 😁", false)));*/
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        new Thread(() -> {

            // Do network action in this function
            try {
                if (socketManager.getWebSocket(requireActivity().getApplication()).isOpen()) {
                    socketManager.getWebSocketState().postValue(SocketManager.WebSocketState.CONNECTED);
                    Log.i(TAG, "connectSocket: opened");
                } else {
                    socketManager.getWebSocket(requireActivity().getApplication()).connect();
                    socketManager.getWebSocketState().postValue(SocketManager.WebSocketState.STARTED);
                    //socketManager.callback = new PingChatAdapter();
                }
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        }).start();

        SocketManager.getMutableMessageList().observe(getViewLifecycleOwner(), messageList -> {
            pingChatRVAdapter.setList(messageList);
            Log.i(TAG, "getMutableMessageList: ");
        });
    }

    private void sendMessage() {
        String message = binding.messageEntry.getText().toString();
        if (message.length() > 0) {
            Utils.hideKeyboardFrom(binding.messageEntry, requireContext());
            Log.i(TAG, "sendMessage: "+ message);
            if (!socketManager.sendMessage(message)) {
                Log.i(TAG, "sendMessage: error");
            }
            binding.messageEntry.getText().clear();
        }

    }

    public void removeChat(boolean totally) {
        Utils.hideKeyboardFrom(binding.messageEntry, requireContext());
        if (totally)
            requireActivity().finish();
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                removeChat(true);
            case R.id.sendButton:
                sendMessage();
        }
    }

    private class PingChatAdapter implements PingChatCallback {
        @Override
        public void onReceivedText(String text) {
            SocketManager.getMutableMessageList().getValue().add(new Message(text, false));
            Log.i(TAG, "onReceivedText: " + text);
            //mutableMessageList.postValue(mutableMessageList.getValue());
        }

        @Override
        public void onError(String error) {
            try {
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

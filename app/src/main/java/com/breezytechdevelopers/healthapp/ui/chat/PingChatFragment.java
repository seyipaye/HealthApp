package com.breezytechdevelopers.healthapp.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.breezytechdevelopers.healthapp.database.entities.Message;
import com.breezytechdevelopers.healthapp.databinding.FragmentPingChatBinding;
import com.breezytechdevelopers.healthapp.utils.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class PingChatFragment extends Fragment {

    private PingChatViewModel pingChatViewModel;
    private FragmentPingChatBinding binding;
    PingChatRVAdapter pingChatRVAdapter;
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private OkHttpClient client;
    private WebSocket webSocket;
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

        pingChatRVAdapter.add(new Message("Hi, this is an echo web socket, " +
                "It say's exactly what you say, try it out 😁", false));

        binding.sendButton.setOnClickListener(view -> {
            String message = binding.messageEntry.getText().toString();
            if (message.length() > 0) {
                Utils.hideKeyboardFrom(view, requireContext());
                sendMessage(message);
                binding.messageEntry.getText().clear();
            }
        });
        client = new OkHttpClient();
        start();
        return binding.getRoot();
    }

    private void sendMessage(String text) {
        webSocket.send(text);
        requireActivity().runOnUiThread(() ->
                pingChatRVAdapter.add(new Message(text, true)));
    }

    private void start() {
        Request request = new Request.Builder()
                .url("ws://echo.websocket.org")
                .build();

        ChatWebSocketListener listener = new ChatWebSocketListener();
        webSocket = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(String text) {
        requireActivity().runOnUiThread(() ->
                pingChatRVAdapter.add(new Message(text, false)));
    }

    private final class ChatWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            //webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            output(text);
        }
        @Override
        public void onMessage(@NonNull WebSocket webSocket, ByteString bytes) {
            output(bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Toast.makeText(requireActivity(), "Closing : " + code + " / " + reason, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onFailure(@NonNull WebSocket webSocket, Throwable t, Response response) {
            Log.e(TAG, "onFailure: "+ t.getMessage());
            Toast.makeText(requireActivity(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

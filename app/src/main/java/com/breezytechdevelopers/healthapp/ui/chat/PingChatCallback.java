package com.breezytechdevelopers.healthapp.ui.chat;

import com.breezytechdevelopers.healthapp.database.entities.Message;

public interface PingChatCallback {
    void onReceivedText(String text);
    void onError(String error);
}

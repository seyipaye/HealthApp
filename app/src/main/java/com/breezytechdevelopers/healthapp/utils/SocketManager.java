package com.breezytechdevelopers.healthapp.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.AppExecutors;
import com.breezytechdevelopers.healthapp.HealthApp;
import com.breezytechdevelopers.healthapp.database.entities.Message;
import com.breezytechdevelopers.healthapp.ui.chat.PingChatCallback;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketManager {
    private static Context mContext;
    private static MutableLiveData<WebSocketState> webSocketState;
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static PingChatCallback pingChatCallback;
    private static AppExecutors mAppExecutors;
    public PingChatCallback callback;

    public void addMessage(Message message) {
        ((HealthApp) mContext).addToMessageList(message);
    }

    public enum WebSocketState {
        CONNECTED,
        INACTIVE,
        STARTED,
        DISCONNECTED
    }

    private static SocketManager instance;

    private SocketManager(Context context) {
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static SocketManager getInstance(Context context, AppExecutors appExecutors) {
        if (instance == null) {
            mContext = context.getApplicationContext();
            mAppExecutors = appExecutors;
            instance = new SocketManager(context);
            webSocketState = new MutableLiveData<>(WebSocketState.INACTIVE);
        }
        return instance;
    }

    /**
     * The constant TAG.
     */
    public static final String TAG = SocketManager.class.getSimpleName();
    private WebSocket webSocket;

    public WebSocket getWebSocket(String pingID, String token, String initialMessage) {
        try {
            if(webSocket == null){
                /*webSocket = new WebSocketFactory().createSocket("ws://echo.websocket.org", 5000);*/

                webSocket = new WebSocketFactory().createSocket("wss://curefb.herokuapp.com/ws/chat/" + pingID, 30000);
                webSocket.addProtocol(token);
                ((HealthApp) mContext).getMutableMessageList().postValue(new ArrayList<>());
                addMessage(new Message(initialMessage, true));
                Log.i(TAG, "connectSocket: new");
                webSocket.addListener(new WebSocketListener() {
                    @Override
                    public void onStateChanged(WebSocket websocket, com.neovisionaries.ws.client.WebSocketState newState) throws Exception {
                        Log.i(TAG, "onStateChanged: ");
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                        Log.i(TAG, "onConnected: ");
                        webSocketState.postValue(WebSocketState.CONNECTED);
                    }

                    @Override
                    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.i(TAG, "onConnectError: ");
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                        Log.i(TAG, "onDisconnected: ");
                        webSocketState.postValue(WebSocketState.DISCONNECTED);

                    }

                    @Override
                    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onFrame: ");
                    }

                    @Override
                    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onContinuationFrame: ");
                    }

                    @Override
                    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onTextFrame: ");
                    }

                    @Override
                    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onBinaryFrame: ");
                    }

                    @Override
                    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onCloseFrame: ");
                    }

                    @Override
                    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onPingFrame: ");
                    }

                    @Override
                    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onPongFrame: ");
                    }

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        mAppExecutors.networkIO().execute(() -> {
                            Log.i(TAG, "onTextMessage: " + text);
                            webSocketState.postValue(WebSocketState.CONNECTED);
                            try {
                                JSONObject jsonData = new JSONObject(text).getJSONObject("data");
                                if (!jsonData.has("status")) {
                                    ((HealthApp) mContext).addToMessageList(new Message(jsonData.getString("message"), false));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ((HealthApp) mContext).addToMessageList(new Message(text, false));
                            }
                        });
                    }

                    @Override
                    public void onTextMessage(WebSocket websocket, byte[] data) throws Exception {
                        Log.i(TAG, "onTextMessage: ");
                    }

                    @Override
                    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                        Log.i(TAG, "onBinaryMessage: ");
                    }

                    @Override
                    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onSendingFrame: ");
                    }

                    @Override
                    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onFrameSent: ");
                    }

                    @Override
                    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onFrameUnsent: ");
                    }

                    @Override
                    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
                        Log.i(TAG, "onThreadCreated: ");
                    }

                    @Override
                    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
                        Log.i(TAG, "onThreadStarted: ");
                    }

                    @Override
                    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
                        Log.i(TAG, "onThreadStopping: ");
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.i(TAG, "onError: ");
                        pingChatCallback.onError(cause.toString());
                    }

                    @Override
                    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onFrameError: ");
                    }

                    @Override
                    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                        Log.i(TAG, "onMessageError: ");
                    }

                    @Override
                    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
                        Log.i(TAG, "onMessageDecompressionError: ");
                    }

                    @Override
                    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                        Log.i(TAG, "onTextMessageError: ");
                    }

                    @Override
                    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                        Log.i(TAG, "onSendError: ");
                    }

                    @Override
                    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.i(TAG, "onUnexpectedError: ");
                    }

                    @Override
                    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
                        Log.i(TAG, "handleCallbackError: ");
                    }

                    @Override
                    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
                        Log.i(TAG, "onSendingHandshake: ");
                    }
                });
            } else {
                Log.i(TAG, "connectSocket: existing");
            }
/*
            webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    webSocketState.setValue(WebSocketState.CONNECTED);
                    Log.i(TAG, "onConnected: ");
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    webSocketState.setValue(WebSocketState.CONNECTED);
                    mutableMessageList.getValue().add(new Message(text, false));
                    mutableMessageList.postValue(mutableMessageList.getValue());

                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    webSocketState.postValue(WebSocketState.DISCONNECTED);
                    //webSocket.close(NORMAL_CLOSURE_STATUS, null);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);
                    Log.e(TAG, "onFailure: "+ cause);
                    pingChatCallback.onErrorOccur(cause.toString());
                }
            });
*/
            //send {"status": "ignored"} if {"status": "accepted"}
            return webSocket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webSocket;
    }

    /**
     * Connect socket.
     */


    public void storeReceivedMessage(String text) {
        //((HealthApp) mContext).
    }
    public boolean sendMessage(String text) {
        if (webSocket.isOpen()) {
            mAppExecutors.networkIO().execute(() -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "send_message");
                jsonObject.addProperty("data", text);
                Log.i(TAG, "sendMessage: " + jsonObject.toString());
                webSocket.sendText(jsonObject.toString());
                ((HealthApp) mContext).addToMessageList(new Message(text, true));

            });
            return true;
        } else {
            return false;
        }
    }

    protected Object readResolve(){
        return getInstance(mContext, mAppExecutors);
    }

    /**
     * Destroy.
     */
    public void destroy(){
        if (webSocket != null) {
            webSocket.sendClose(NORMAL_CLOSURE_STATUS, "Goodbye !");
            webSocketState.postValue(WebSocketState.DISCONNECTED);
            webSocket = null;
        }
    }

    public MutableLiveData<WebSocketState> getWebSocketState() {
        return webSocketState;
    }

    /**
     * The WebSocketListener.
     */

//    public static class NetReceiver extends BroadcastReceiver {
//
//        /**
//         * The Tag.
//         */
//        public final String TAG = NetReceiver.class.getSimpleName();
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager cm =
//                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            boolean isConnected = activeNetwork != null &&
//                    activeNetwork.isConnectedOrConnecting();
//
//            SocketManager.getInstance().fireInternetStatusIntent(
//                    isConnected?SocketManager.STATE_CONNECTED:SocketManager.STATE_DISCONNECTED);
//            if (isConnected) {
//                if(SocketManager.getInstance().getWebSocket()!=null
//                        && !SocketManager.getInstance().getWebSocket().connected()){
//                    SocketManager.getInstance().fireSocketStatus(SocketManager.STATE_CONNECTING);
//                }
//                PowerManager powerManager =
//                        (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//                boolean isScreenOn;
//                if (android.os.Build.VERSION.SDK_INT
//                        >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
//                    isScreenOn = powerManager.isInteractive();
//                }else{
//                    //noinspection deprecation
//                    isScreenOn = powerManager.isScreenOn();
//                }
//
//                if (isScreenOn && SocketManager.getInstance().getWebSocket() !=null) {
//                    Log.d(TAG, "NetReceiver: Connecting Socket");
//                    if(!SocketManager.getInstance().getWebSocket().connected()){
//                        SocketManager.getInstance().getWebSocket().connect();
//                    }
//                }
//            }else{
//                SocketManager.getInstance().fireSocketStatus(SocketManager.STATE_DISCONNECTED);
//                if(SocketManager.getInstance().getWebSocket() !=null){
//                    Log.d(TAG, "NetReceiver: disconnecting socket");
//                    SocketManager.getInstance().getWebSocket().disconnect();
//                }
//            }
//        }
//    }
}

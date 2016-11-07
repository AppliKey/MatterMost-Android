package com.applikey.mattermost.platform;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.socket.MessagePostedEventData;
import com.applikey.mattermost.models.socket.Props;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class WebSocketService extends Service {

    private static final String TAG = WebSocketService.class.getSimpleName();

    private static final String EVENT_STATUS_CHANGE = "status_change";
    private static final String EVENT_TYPING = "typing";
    private static final String EVENT_MESSAGE_POSTED = "posted";

    @Inject
    Prefs mPrefs;

    @Inject
    BearerTokenFactory mTokenFactory;

    @Inject
    PostStorage mPostStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    ErrorHandler mErrorHandler;

    private WebSocket mWebSocket;
    private Handler mHandler;
    private final WebSocketListener mWebSocketAdapter = new WebSocketListener() {

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
            Log.d(TAG, "onStateChanged: ");
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            Log.d(TAG, "onConnected: socket connected!");
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            Log.d(TAG, text);
            handleMessage(text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            Log.d(TAG, "onBinaryMessage: ");
        }

        @Override
        public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onSendingFrame: ");
        }

        @Override
        public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onFrameSent: ");
        }

        @Override
        public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onFrameUnsent: ");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            Log.e(TAG, "onError: ", cause);
        }

        @Override
        public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
            Log.e(TAG, "onFrameError: ", cause);
        }

        @Override
        public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
            Log.e(TAG, "onMessageError: ", cause);
        }

        @Override
        public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
            Log.e(TAG, "onMessageDecompressionError: ", cause);
        }

        @Override
        public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
            Log.e(TAG, "onTextMessageError: ", cause);
        }

        @Override
        public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
            Log.e(TAG, "onSendError: ", cause);
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
            Log.e(TAG, "onUnexpectedError: ", cause);
        }

        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
            Log.e(TAG, "handleCallbackError: ", cause);
        }

        @Override
        public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
            Log.d(TAG, "onSendingHandshake: ");
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            Log.d(TAG, "onDisconnected: ");
            mWebSocket = mWebSocket.recreate().connectAsynchronously();
        }

        @Override
        public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onFrame: ");
        }

        @Override
        public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onContinuationFrame: ");
        }

        @Override
        public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onTextFrame: ");
        }

        @Override
        public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onBinaryFrame: ");
        }

        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onCloseFrame: ");
        }

        @Override
        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onPingFrame: ");
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            Log.d(TAG, "onPongFrame: ");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            Log.e(TAG, "onConnectError: ", exception);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        App.getUserComponent().inject(this);

        mHandler = new Handler(Looper.getMainLooper());
        try {
            openSocket();
        } catch (IOException | WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "closing socket");

        closeSocket();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void openSocket() throws IOException, WebSocketException {
        Log.d(TAG, "Opening socket");

        if (mWebSocket != null) {
            mWebSocket.disconnect();
        }

        String baseUrl = mPrefs.getCurrentServerUrl();
        baseUrl = UrlUtil.removeProtocol(baseUrl);
        baseUrl = UrlUtil.WEB_SERVICE_PROTOCOL_PREFIX + baseUrl;
        baseUrl = baseUrl + Constants.WEB_SOCKET_ENDPOINT;

        mWebSocket = new WebSocketFactory()
                .setConnectionTimeout(Constants.WEB_SOCKET_TIMEOUT)
                .createSocket(baseUrl);

        mWebSocket.addListener(mWebSocketAdapter);
        mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mTokenFactory.getBearerTokenString());
        mWebSocket.connectAsynchronously();
    }

    private void handleMessage(String message) {
        final Gson gson = GsonFactory.INSTANCE.getGson();
        final WebSocketEvent event = gson.fromJson(message, WebSocketEvent.class);

        String eventType = event.getEvent();
        // Mattermost Old API fix
        if (eventType == null) {
            eventType = event.getAction();
        }

        Log.d(TAG, "Got event: " + eventType);

        switch (eventType) {
            case EVENT_MESSAGE_POSTED: {
                Log.d(TAG, "Extracting message");

                final Post post = extractPostFromSocket(gson, event);
                Log.d(TAG, "Post message: " + post.getMessage());

                mHandler.post(() -> {
                    mPostStorage.save(post);
                    mChannelStorage.findByIdAndCopy(post.getChannelId())
                            .first()
                            .doOnNext(channel -> channel.setLastPost(post))
                            .subscribe(mChannelStorage::updateLastPost, mErrorHandler::handleError);
                });
            }
        }
    }

    private void closeSocket() {
        mWebSocket.removeListener(mWebSocketAdapter);
        mWebSocket.sendClose();
        mWebSocket.disconnect();
    }

    private Post extractPostFromSocket(Gson gson, WebSocketEvent event) {
        final JsonObject eventData = event.getData();
        final String postObject;
        if (eventData != null) {
            final MessagePostedEventData data = gson.fromJson(eventData, MessagePostedEventData.class);
            postObject = data.getPostObject();
        } else {
            final JsonObject eventProps = event.getProps();
            final Props props = gson.fromJson(eventProps, Props.class);
            postObject = props.getPost();
        }
        return gson.fromJson(postObject, Post.class);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }
}

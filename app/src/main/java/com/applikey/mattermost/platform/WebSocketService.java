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
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.ErrorHandler;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

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

    private WebSocket mWebSocket;
    private Handler mHandler;

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

        try {
            openSocket();
        } catch (IOException | WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }

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

        final Gson gson = new Gson();

        mWebSocket.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                super.onTextMessage(websocket, text);
                Log.d(TAG, text);

                handleMessage(gson, text);
            }
        });

        mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mTokenFactory.getBearerTokenString());

        mWebSocket.connectAsynchronously();
        Log.d(TAG, "Socket opened");
    }

    private void handleMessage(Gson gson, String message) {
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

                    mChannelStorage
                            .findByIdAndCopy(post.getChannelId())
                            .first()
                            .doOnNext(channel -> {
                                channel.setLastPost(post);
                                channel.setLastPostAt(post.getCreatedAt());
                                mChannelStorage.updateLastPost(channel);
                            })
                            .subscribe(v -> {
                            }, ErrorHandler::handleError);
                });
            }
        }
    }

    private void closeSocket() {
        mWebSocket.sendClose();
        mWebSocket.disconnect();
    }

    private Post extractPostFromSocket(Gson gson, WebSocketEvent event) {
        final MessagePostedEventData data = gson.fromJson(event.getData(), MessagePostedEventData.class);
        final String postObject = data.getPostObject();
        return gson.fromJson(postObject, Post.class);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }
}

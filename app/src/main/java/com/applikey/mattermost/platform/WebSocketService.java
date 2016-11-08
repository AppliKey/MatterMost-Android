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
import com.applikey.mattermost.utils.ConnectivityUtils;
import com.applikey.mattermost.utils.kissUtils.utils.NetworkUtil;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.utils.rx.RetrySocketConnection;
import com.applikey.mattermost.utils.rx.RetryWhenNetwork;
import com.applikey.mattermost.utils.rx.RetryWithDelay;
import com.applikey.mattermost.utils.rx.RxUtils;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.GsonFactory;
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private MessagingSocket mMessagingSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        App.getUserComponent().inject(this);

        mHandler = new Handler(Looper.getMainLooper());

        String baseUrl = mPrefs.getCurrentServerUrl();
        baseUrl = UrlUtil.removeProtocol(baseUrl);
        baseUrl = UrlUtil.WEB_SERVICE_PROTOCOL_PREFIX + baseUrl;
        baseUrl = baseUrl + Constants.WEB_SOCKET_ENDPOINT;

        mMessagingSocket = new MessagingSocket(mTokenFactory);
        mMessagingSocket.listen(baseUrl)
                .retryWhen(new RetrySocketConnection(this))
                .subscribe(this::handleSocketEvent, mErrorHandler::handleError);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "closing socket");
        disconnectSocket();
    }

    private void disconnectSocket() {
        if (mMessagingSocket.isConnected()) {
            mMessagingSocket.disconnect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleSocketEvent(WebSocketEvent event) {
        String eventType = event.getEvent();
        // Mattermost Old API fix
        if (eventType == null) {
            eventType = event.getAction();
        }

        Log.d(TAG, "Got event: " + eventType);

        switch (eventType) {
            case EVENT_MESSAGE_POSTED: {
                Log.d(TAG, "Extracting message");

                final Post post = extractPostFromSocket(event);
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

    private Post extractPostFromSocket(WebSocketEvent event) {
        final Gson gson = GsonFactory.INSTANCE.getGson();
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

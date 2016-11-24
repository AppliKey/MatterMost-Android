package com.applikey.mattermost.interactor;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.platform.socket.Socket;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessagingInteractor {

    private static final String TAG = "MessagingInteractor";

    private final Context mContext;
    private final ChannelStorage mChannelStorage;
    private final UserStorage mUserStorage;
    private final PostStorage mPostStorage;
    private final Socket mMessagingSocket;
    private final Api mApi;
    private final Gson mGson;
    private final ErrorHandler mErrorHandler;
    private final Handler mHandler;

    public MessagingInteractor(Context context, ChannelStorage channelStorage,
            UserStorage userStorage,
            PostStorage postStorage,
            Socket messagingSocket, Api api, Gson gson, ErrorHandler errorHandler) {
        mContext = context;
        mChannelStorage = channelStorage;
        mUserStorage = userStorage;
        mPostStorage = postStorage;
        mMessagingSocket = messagingSocket;
        mApi = api;
        mGson = gson;
        mErrorHandler = errorHandler;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public Observable<Void> pollUserStatuses() {
        return Observable.interval(Constants.POLLING_PERIOD_SECONDS, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(tick -> mUserStorage.listDirectProfiles().first())
                .observeOn(Schedulers.io())
                .flatMap(users -> mApi.getUserStatusesCompatible(Stream.of(users)
                        .map(User::getId)
                        .collect(Collectors.toList())
                        .toArray(new String[users.size()])))
                .doOnNext(mUserStorage::updateUsersStatuses)
                .map(users -> null);
    }

    public Observable<WebSocketEvent> listenMessagingSocket() {
        return mMessagingSocket.listen()
                .subscribeOn(Schedulers.io())
                .doOnNext(event -> Log.d(TAG, "listenMessagingSocket: event: " + event.getEvent()));
    }

    private void handleSocketEvent(WebSocketEvent event) {
        String eventType = event.getEvent();

        Log.d(TAG, "Got event: " + eventType);

        switch (eventType) {
            case WebSocketEvent.EVENT_POST_POSTED:
                final Post post = event.getProps().getPost();
                Log.d(TAG, "Post message: " + post.getMessage());
                mHandler.post(() -> {
                    mPostStorage.save(post);
                });
                mChannelStorage.get(post.getChannelId())
                        .first()
                        .doOnNext(channel -> channel.setLastPost(post))
                        .subscribe(channel -> Log.d(TAG, "CONGRATULATIONS! CHANNEL FROM REALM: " + channel.getDisplayName()), mErrorHandler::handleError);
                break;

            case WebSocketEvent.EVENT_CHANNEL_VIEWED:
                Log.d(TAG, "View channel: " + event.getChannelId());
                mHandler.post(() -> {
                    mChannelStorage.updateLastViewedAt(event.getChannelId());
                });
                break;
        }
    }

}

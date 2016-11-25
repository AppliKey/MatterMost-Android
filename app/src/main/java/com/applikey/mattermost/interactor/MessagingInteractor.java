package com.applikey.mattermost.interactor;


import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.platform.socket.Socket;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.applikey.mattermost.models.socket.WebSocketEvent.EVENT_CHANNEL_VIEWED;
import static com.applikey.mattermost.models.socket.WebSocketEvent.EVENT_POST_POSTED;

public class MessagingInteractor {

    private static final String TAG = "MessagingInteractor";

    private final ChannelStorage mChannelStorage;
    private final UserStorage mUserStorage;
    private final PostStorage mPostStorage;
    private final Socket mMessagingSocket;
    private final Api mApi;

    public MessagingInteractor(ChannelStorage channelStorage,
            UserStorage userStorage,
            PostStorage postStorage,
            Socket messagingSocket, Api api) {
        mChannelStorage = channelStorage;
        mUserStorage = userStorage;
        mPostStorage = postStorage;
        mMessagingSocket = messagingSocket;
        mApi = api;
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
                .doOnNext(event -> {
                    if (EVENT_POST_POSTED.equals(event.getEvent())) {
                        mChannelStorage.updateLastPost(event.getChannelId(), event.getProps().getPost());
                    } else if (EVENT_CHANNEL_VIEWED.equals(event.getEvent())) {
                        mChannelStorage.updateViewedAt(event.getChannelId());
                    }
                });
    }

}

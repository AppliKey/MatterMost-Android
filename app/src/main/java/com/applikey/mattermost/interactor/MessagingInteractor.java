package com.applikey.mattermost.interactor;


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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.applikey.mattermost.models.socket.WebSocketEvent.EVENT_CHANNEL_VIEWED;
import static com.applikey.mattermost.models.socket.WebSocketEvent.EVENT_POST_DELETED;
import static com.applikey.mattermost.models.socket.WebSocketEvent.EVENT_POST_EDITED;
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

    public Single<Map<String, String>> pollUserStatuses() {
        return Observable.interval(Constants.POLLING_PERIOD_SECONDS, TimeUnit.SECONDS)
                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(tick -> mUserStorage.listDirectProfiles().first().toSingle())
                .observeOn(Schedulers.io())
                .flatMap(users -> mApi.getUserStatusesCompatible(Stream.of(users)
                        .map(User::getId)
                        .collect(Collectors.toList())
                        .toArray(new String[users.size()])))
                .doOnSuccess(mUserStorage::updateUsersStatuses);
    }

    public Observable<WebSocketEvent> listenMessagingSocket() {
        return mMessagingSocket.listen()
                .subscribeOn(Schedulers.io())
                .doOnNext(event -> {
                    final Post post = event.getProps().getPost();
                    final String socketEvent = event.getEvent();
                    if (EVENT_POST_POSTED.equals(socketEvent)) {
                        mChannelStorage.updateLastPost(event.getChannelId(), post);
                    } else if (EVENT_POST_EDITED.equals(socketEvent)) {
                        mPostStorage.saveSync(post);
                    } else if (EVENT_CHANNEL_VIEWED.equals(socketEvent)) {
                        mChannelStorage.updateViewedAt(event.getChannelId());
                    } else if (EVENT_POST_DELETED.equals(socketEvent)) {
                        mPostStorage.deleteSync(post);
                    }
                });
    }

}

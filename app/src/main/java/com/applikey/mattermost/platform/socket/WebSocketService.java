package com.applikey.mattermost.platform.socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.socket.MessagePostedEventData;
import com.applikey.mattermost.models.socket.Props;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class WebSocketService extends Service {

    private static final String TAG = WebSocketService.class.getSimpleName();

    private static final String EVENT_STATUS_CHANGE = "status_change";
    private static final String EVENT_TYPING = "typing";
    private static final String EVENT_MESSAGE_POSTED = "posted";

    @Inject
    PostStorage mPostStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    Socket mMessagingSocket;

    @Inject
    Api mApi;

    @Inject
    UserStorage mUserStorage;

    private Handler mHandler;
    private CompositeSubscription mCompositeSubscription;
    private Subscription mPollingSubscription;

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getUserComponent().inject(this);
        mHandler = new Handler(Looper.getMainLooper());
        mCompositeSubscription = new CompositeSubscription();
        openSocket();
        startPollingUsersStatuses();
    }

    private void openSocket() {
        mCompositeSubscription.add(mMessagingSocket.listen()
                .retryWhen(mErrorHandler::tryReconnectSocket)
                .observeOn(Schedulers.computation())
                .subscribe(this::handleSocketEvent, mErrorHandler::handleError));
    }

    private void startPollingUsersStatuses() {
        mPollingSubscription = Observable.interval(Constants.POLLING_PERIOD_SECONDS, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(tick -> mUserStorage.listDirectProfiles().first())
                .observeOn(Schedulers.io())
                .flatMap(users -> mApi.getUserStatusesCompatible(Stream.of(users)
                        .map(User::getId)
                        .collect(Collectors.toList())
                        .toArray(new String[users.size()])))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(usersStatusesMap -> {
                    Timber.d("updating users statuses");
                    mUserStorage.updateUsersStatuses(usersStatusesMap);
                })
                .subscribe();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
        if (mPollingSubscription != null && !mPollingSubscription.isUnsubscribed()) {
            mPollingSubscription.unsubscribe();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void closeSocket() {
        mMessagingSocket.close();
        mCompositeSubscription.unsubscribe();
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
}

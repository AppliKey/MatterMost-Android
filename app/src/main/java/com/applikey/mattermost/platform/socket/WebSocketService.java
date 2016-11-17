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
import com.applikey.mattermost.manager.ForegroundManager;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.socket.MessagePostedEventData;
import com.applikey.mattermost.models.socket.Props;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.utils.rx.RetryWhenNetwork;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class WebSocketService extends Service {

    private static final String TAG = WebSocketService.class.getSimpleName();

    private static final String EVENT_STATUS_CHANGE = "status_change";
    private static final String EVENT_TYPING = "typing";
    private static final String EVENT_MESSAGE_POSTED = "posted";
    private static final String EVENT_CHANNEL_VIEWED = "channel_viewed";

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

    @Inject
    ForegroundManager mForegroundManager;

    @Inject
    Gson mGson;

    private Handler mHandler;
    private CompositeSubscription mSubscriptions;

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getUserComponent().inject(this);

        mHandler = new Handler(Looper.getMainLooper());
        mSubscriptions = new CompositeSubscription();

        openSocket();
//        startPollingUsersStatuses();
        mForegroundManager.foreground()
                .doOnNext(foreground -> Log.d(TAG, "Application is " + (foreground ? "foreground" : "background")))
                .switchMap(foreground -> foreground ? Observable.never() : Observable.timer(10, TimeUnit.SECONDS))
                .doOnNext(next -> Log.d(TAG, "Application in background for 10 secs!"))
                .subscribe(background -> stopSelf(), Throwable::printStackTrace);
    }

    private void openSocket() {
        mSubscriptions.add(mMessagingSocket.listen()
                .subscribeOn(Schedulers.io())
                .retryWhen(mErrorHandler::tryReconnectSocket)
                .observeOn(Schedulers.computation())
                .subscribe(this::handleSocketEvent, mErrorHandler::handleError));
    }

    private void startPollingUsersStatuses() {
        mSubscriptions.add(Observable.interval(Constants.POLLING_PERIOD_SECONDS, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(tick -> mUserStorage.listDirectProfiles().first())
                .observeOn(Schedulers.io())
                .flatMap(users -> mApi.getUserStatusesCompatible(Stream.of(users)
                        .map(User::getId)
                        .collect(Collectors.toList())
                        .toArray(new String[users.size()])))
                .retryWhen(new RetryWhenNetwork(this))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(usersStatusesMap -> {
                    Timber.d("updating users statuses");
                    mUserStorage.updateUsersStatuses(usersStatusesMap);
                })
                .subscribe(ignore -> {
                }, mErrorHandler::handleError));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Terminate Service");
        mMessagingSocket.close();
        mSubscriptions.unsubscribe();
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
            case EVENT_MESSAGE_POSTED:
                final Post post = extractPostFromSocket(event);
                Log.d(TAG, "Post message: " + post.getMessage());
                mHandler.post(() -> {
                    mPostStorage.save(post);
                    mChannelStorage.findByIdAndCopy(post.getChannelId())
                            .first()
                            .doOnNext(channel -> channel.setLastPost(post))
                            .subscribe(mChannelStorage::updateLastPost, mErrorHandler::handleError);
                });
                break;

            case EVENT_CHANNEL_VIEWED:
                Log.d(TAG, "View channel: " + event.getChannelId());
                mHandler.post(() -> mChannelStorage.updateLastViewedAt(event.getChannelId()));
                break;
        }
    }

    private Post extractPostFromSocket(WebSocketEvent event) {
        final JsonObject eventData = event.getData();
        final String postObject;
        if (eventData != null) {
            final MessagePostedEventData data = mGson.fromJson(eventData, MessagePostedEventData.class);
            postObject = data.getPostObject();
        } else {
            final JsonObject eventProps = event.getProps();
            final Props props = mGson.fromJson(eventProps, Props.class);
            postObject = props.getPost();
        }
        return mGson.fromJson(postObject, Post.class);
    }
}

package com.applikey.mattermost.platform.socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.interactor.MessagingInteractor;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.rx.RetryWhenNetwork;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.concurrent.TimeUnit;

public class WebSocketService extends Service {

    private static final String TAG = WebSocketService.class.getSimpleName();

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    MessagingInteractor mMessagingInteractor;

    private CompositeSubscription mSubscriptions;

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getUserComponent().inject(this);
        Log.d(TAG, "Service started");

        mSubscriptions = new CompositeSubscription();

        listenSocket();
        startPollingUsersStatuses();
    }

    private void listenSocket() {
        final Subscription socketSubscription = mMessagingInteractor.listenMessagingSocket()
                .retryWhen(mErrorHandler::tryReconnectSocket)
                .subscribe(event -> Log.d(TAG, "Socket event received: " + event.getEvent()),
                           mErrorHandler::handleError);
        mSubscriptions.add(socketSubscription);
    }

    private void startPollingUsersStatuses() {
        final Subscription pollStatusesObservable = mMessagingInteractor.pollUserStatuses()
                .retryWhen(new RetryWhenNetwork(this))
                .subscribe(updated -> Log.d(TAG, "Users statuses updated"), mErrorHandler::handleError);
        mSubscriptions.add(pollStatusesObservable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");

        mSubscriptions.unsubscribe();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

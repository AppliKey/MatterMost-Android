package com.applikey.mattermost.web;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.applikey.mattermost.App;
import com.applikey.mattermost.HttpCode;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.injects.PerApp;
import com.applikey.mattermost.platform.SocketConnectionException;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.SettingsManager;
import com.applikey.mattermost.utils.ConnectivityUtils;
import com.applikey.mattermost.utils.rx.RetryWhenNetwork;
import com.applikey.mattermost.utils.rx.RetryWithDelay;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

@PerApp
public final class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();
    private static final int MAX_ATTEMPT_NUMBER = 5;

    private final Context mContext;
    private final SettingsManager mSettingsManager;
    private final StorageDestroyer mStorageDestroyer;

    @Inject
    public ErrorHandler(Context context, SettingsManager settingsManager,
            StorageDestroyer storageDestroyer) {
        this.mContext = context;
        this.mSettingsManager = settingsManager;
        this.mStorageDestroyer = storageDestroyer;
    }

    public void handleError(Throwable throwable) {
        if (handleApiException(throwable)) {
            return;
        }

        Log.e(TAG, throwable.getMessage());
    }

    public void handleError(String message) {
        Log.e(TAG, message);
    }

    public Observable<?> tryReconnectSocket(Observable<? extends Throwable> attempt) {
        return attempt.flatMap(throwable -> {
            if (throwable instanceof SocketConnectionException) {
                return ConnectivityUtils.getConnectivityObservable(mContext).takeFirst(status -> status);
            } else {
                return Observable.range(1, MAX_ATTEMPT_NUMBER).flatMap(i -> i == MAX_ATTEMPT_NUMBER
                        ? Observable.error(throwable)
                        : Observable.timer(2 << i, TimeUnit.SECONDS, Schedulers.immediate()));
            }
        });
    }

    private boolean handleApiException(Throwable throwable) {
        if (throwable instanceof HttpException) {
            final HttpException exception = (HttpException) throwable;
            if (exception.code() == HttpCode.UNAUTHORIZED) {
                handleUnauthorizedException();
                return true;
            }
        }
        return false;
    }

    private void handleUnauthorizedException() {
        Toast.makeText(mContext, R.string.your_session_has_expired, Toast.LENGTH_SHORT).show();

        mSettingsManager.deleteUserSession();
        mStorageDestroyer.deleteDatabase();
        App.releaseUserComponent();

        mContext.startActivity(ChooseServerActivity.getIntent(mContext, true));
    }
}

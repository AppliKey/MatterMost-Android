package com.applikey.mattermost.web;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.applikey.mattermost.App;
import com.applikey.mattermost.HttpCode;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.injects.PerApp;
import com.applikey.mattermost.models.web.RequestError;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.SettingsManager;
import com.applikey.mattermost.utils.ConnectivityUtils;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@PerApp
public final class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();
    private static final int MAX_ATTEMPT_NUMBER = 3;

    private final Context mContext;
    private final SettingsManager mSettingsManager;
    private final StorageDestroyer mStorageDestroyer;
    private final TypeAdapter<RequestError> mJsonErrorAdapter;

    @Inject
    public ErrorHandler(Context context, SettingsManager settingsManager,
            StorageDestroyer storageDestroyer, Gson gson) {
        mContext = context;
        mSettingsManager = settingsManager;
        mStorageDestroyer = storageDestroyer;
        mJsonErrorAdapter = gson.getAdapter(RequestError.class);
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

    public Observable<?> tryReconnectSocket(Observable<? extends Throwable> errors) {
        final AtomicInteger attemptCount = new AtomicInteger(0);
        return errors
                .doOnNext(next -> Log.d(TAG, "tryReconnectSocket: error arrived!"))
                .debounce(10, TimeUnit.SECONDS, Schedulers.immediate())
                .doOnNext(next -> Log.d(TAG, "tryReconnectSocket: attempt #" + attemptCount.incrementAndGet() + ", start listening to network status"))
                .switchMap(error -> ConnectivityUtils.getConnectivityObservable(mContext).takeFirst(status -> status))
                .doOnNext(next -> Log.d(TAG, "tryReconnectSocket: network is available, reconnect started!"));
    }

    private boolean handleApiException(Throwable throwable) {
        if (isHttpError(throwable)) {
            final HttpException exception = (HttpException) throwable;
            if (exception.code() == HttpCode.UNAUTHORIZED) {
                handleUnauthorizedException();
                return true;
            }
        }
        return false;
    }

    public String getErrorMessage(Throwable e) {
        String errorMessage = mContext.getString(R.string.unknown_error);
        if (isHttpError(e)) {
            final HttpException httpException = (HttpException) e;
            if (isHttpExceptionWithCode(httpException, HttpCode.INTERNAL_SERVER_ERROR)) {
                final Response<?> responseBody = httpException.response();
                try {
                    final RequestError requestError = getErrorModel(responseBody, mJsonErrorAdapter);
                    if (requestError != null) {
                        errorMessage = getErrorMessage(requestError, requestError.getMessage());
                    }
                } catch (IOException ioe) {
                    Timber.e(ioe);
                }
            }
        } else {
            handleError(e);
        }
        return errorMessage;
    }

    private String getErrorMessage(RequestError requestError, String defaultMessage) {
        switch (requestError.getId()) {
            case MattermostErrorIds.CHANNEL_ALREADY_CREATED: {
                return mContext.getString(R.string.error_channel_already_created);
            }
            case MattermostErrorIds.CHANNEL_URL_EXISTED: {
                //TODO Create a appropriate error message for the case when channel url existed
                return defaultMessage;
            }
            default:
                return defaultMessage;
        }

    }

    @Nullable
    private <T> T getErrorModel(Response response, TypeAdapter<T> parser) throws IOException {
        final ResponseBody responseBody = response.errorBody();
        if (responseBody == null) {
            return null;
        }
        return parser.fromJson(responseBody.string());
    }

    private boolean isHttpExceptionWithCode(HttpException e, int code) {
        return e.code() == code;
    }

    private boolean isHttpError(Throwable e) {
        return e instanceof HttpException;
    }

    private void handleUnauthorizedException() {
        Toast.makeText(mContext, R.string.your_session_has_expired, Toast.LENGTH_SHORT).show();

        mSettingsManager.deleteUserSession();
        mStorageDestroyer.deleteDatabase();
        App.releaseUserComponent();

        mContext.startActivity(ChooseServerActivity.getIntent(mContext, true));
    }
}

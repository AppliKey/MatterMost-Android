package com.applikey.mattermost.web;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.applikey.mattermost.App;
import com.applikey.mattermost.HttpCode;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.injects.PerApp;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.SettingsManager;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;

@PerApp
public final class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();

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

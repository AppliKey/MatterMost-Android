package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.applikey.mattermost.Constants;

/**
 * Storage, which uses {@link SharedPreferences} to store simple values.
 */
public class Prefs {

    private static final String KEY_USER_ID = Constants.PACKAGE_NAME + ".USER_ID";
    private static final String KEY_SERVER_URL = Constants.PACKAGE_NAME + ".SERVER_URL";
    private static final String KEY_AUTH_TOKEN = Constants.PACKAGE_NAME + ".AUTH_TOKEN";

    private SharedPreferences mSharedPreferences;

    public Prefs(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    public String getCurrentUserId() {
        return mSharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setCurrentUserId(@Nullable String id) {
        mSharedPreferences.edit().putString(KEY_USER_ID, id).apply();
    }

    @Nullable
    public String getCurrentServerUrl() {
        return mSharedPreferences.getString(KEY_SERVER_URL, null);
    }

    public void setCurrentServerUrl(String currentServerUrl) {
        mSharedPreferences.edit().putString(KEY_SERVER_URL, currentServerUrl).apply();
    }

    @Nullable
    public String getAuthToken() {
        return mSharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void setKeyAuthToken(String authToken) {
        mSharedPreferences.edit().putString(KEY_AUTH_TOKEN, authToken).apply();
    }
}

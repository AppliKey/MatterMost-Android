package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.applikey.mattermost.Constants;

import rx.Single;

/**
 * Storage, which uses {@link SharedPreferences} to store simple values.
 */
public class Prefs {

    private static final String KEY_USER_ID = Constants.PACKAGE_NAME + ".USER_ID";
    private static final String KEY_TEAM_ID = Constants.PACKAGE_NAME + ".TEAM_ID";
    private static final String KEY_SERVER_URL = Constants.PACKAGE_NAME + ".SERVER_URL";
    private static final String KEY_AUTH_TOKEN = Constants.PACKAGE_NAME + ".AUTH_TOKEN";
    private static final String KEY_GCM_TOKEN = Constants.PACKAGE_NAME + ".GCM_TOKEN";
    private static final String KEY_TEAM_NAME = Constants.PACKAGE_NAME + ".TEAM_NAME";
    private static final String KEY_SERVER_VERSION = Constants.PACKAGE_NAME + ".SERVER_VERSION";
    private static final String KEY_SERVER_VERSION_MAJOR = Constants.PACKAGE_NAME + ".SERVER_VERSION_MAJOR";
    private static final String KEY_SERVER_VERSION_MINOR = Constants.PACKAGE_NAME + ".SERVER_VERSION_MINOR";

    private final SharedPreferences mSharedPreferences;

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
    public String getCurrentTeamId() {
        return mSharedPreferences.getString(KEY_TEAM_ID, null);
    }

    public void setCurrentTeamId(@Nullable String id) {
        mSharedPreferences.edit().putString(KEY_TEAM_ID, id).apply();
    }

    @Nullable
    public String getCurrentTeamName() {
        return mSharedPreferences.getString(KEY_TEAM_NAME, null);
    }

    public void setCurrentTeamName(@Nullable String id) {
        mSharedPreferences.edit().putString(KEY_TEAM_NAME, id).apply();
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

    public void setAuthToken(@Nullable String authToken) {
        mSharedPreferences.edit().putString(KEY_AUTH_TOKEN, authToken).apply();
    }

    public String getGcmToken() {
        return mSharedPreferences.getString(KEY_GCM_TOKEN, null);
    }

    public void setGcmToken(@Nullable String gcmToken) {
        mSharedPreferences.edit().putString(KEY_GCM_TOKEN, gcmToken).apply();
    }

    public String getServerVersion() {
        return mSharedPreferences.getString(KEY_SERVER_VERSION, null);
    }

    public void setServerVersion(String version) {
        mSharedPreferences.edit().putString(KEY_SERVER_VERSION, version).apply();
    }

    public int getServerVersionMajor() {
        return mSharedPreferences.getInt(KEY_SERVER_VERSION_MAJOR, 0);
    }

    public void setServerVersionMajor(int version) {
        mSharedPreferences.edit().putInt(KEY_SERVER_VERSION_MAJOR, version).apply();
    }

    public int getServerVersionMinor() {
        return mSharedPreferences.getInt(KEY_SERVER_VERSION_MINOR, 0);
    }

    public void setServerVersionMinor(int version) {
        mSharedPreferences.edit().putInt(KEY_SERVER_VERSION_MINOR, version).apply();
    }

    public void setValue(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void setValue(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public Single<String> getValue(String key) {
        return Single.defer(() -> Single.just(mSharedPreferences.getString(key, null)));
    }

    public boolean getValue(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void clear() {
        final String gcmToken = getGcmToken();
        mSharedPreferences.edit().clear().apply();
        setGcmToken(gcmToken);
    }
}

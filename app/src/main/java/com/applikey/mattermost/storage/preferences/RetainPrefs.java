package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.applikey.mattermost.Constants;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;

public class RetainPrefs {

    private static final String TAG = "RetainPrefs";

    private static final String KEY_FAVORITES_CHANNELS = Constants.PACKAGE_NAME + ".FAVORITES_CHANNELS";

    private final SharedPreferences mSharedPreferences;
    private final RxSharedPreferences mRxPreferences;

    public RetainPrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.RETAIN_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        mRxPreferences = RxSharedPreferences.create(mSharedPreferences);
    }

    public void setFavoritesChannels(Set<String> channelIds) {
        mSharedPreferences.edit().putString(KEY_FAVORITES_CHANNELS, TextUtils.join(",", channelIds)).apply();
    }

    public Observable<Set<String>> getFavoritesChannels() {
        return mRxPreferences.getString(KEY_FAVORITES_CHANNELS)
                .asObservable()
                .map(ids -> new HashSet<>(Arrays.asList(TextUtils.split(ids, ","))));
    }

    public Observable<Boolean> addFavoriteChannel(String channelId) {
        return getFavoritesChannels()
                .first()
                .doOnNext(ids -> ids.add(channelId))
                .doOnNext(this::setFavoritesChannels)
                .map(ids -> true);
    }

    public Observable<Boolean> removeFavoriteChannel(String channelId) {
        return getFavoritesChannels()
                .first()
                .doOnNext(ids -> ids.remove(channelId))
                .doOnNext(this::setFavoritesChannels)
                .map(ids -> true);
    }
}

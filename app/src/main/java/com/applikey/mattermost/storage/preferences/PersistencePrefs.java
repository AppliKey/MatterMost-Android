package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.user.UserMetaData;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PersistencePrefs {

    private static final String TAG = "PersistencePrefs";

    private static final String KEY_USERS_METADATA = Constants.PACKAGE_NAME + ".USERS_METADATA";

    private final Prefs mPrefs;
    private final Gson mGson;
    private final SharedPreferences mSharedPreferences;
    private final RxSharedPreferences mRxPreferences;

    private UserMetaData mUserMetaData;

    public PersistencePrefs(Context context, final Prefs prefs, final Gson gson) {
        mPrefs = prefs;
        mGson = gson;

        mSharedPreferences = context.getSharedPreferences(Constants.RETAIN_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        mRxPreferences = RxSharedPreferences.create(mSharedPreferences);
        initUserMetaData();
    }

    private void initUserMetaData() {
        Log.d(TAG, "initUserMetaData: ");
        mRxPreferences.getString(KEY_USERS_METADATA, "")
                .asObservable()
                .first()
                .subscribeOn(Schedulers.io())
                .map(string -> mGson.fromJson(string, new TypeToken<Set<UserMetaData>>() {
                }.getType()))
                .map(o -> (Set<UserMetaData>) o)
                .map(userMetaDatas -> {
                    if (userMetaDatas == null) {
                        userMetaDatas = new HashSet<>(Arrays.asList(new UserMetaData(
                                mPrefs.getCurrentServerUrl(),
                                mPrefs.getCurrentTeamId(),
                                mPrefs.getCurrentUserId())));
                    }
                    return userMetaDatas;
                })
                .flatMap(Observable::from)
                .filter(userMetadata -> userMetadata.getUserId().equals(mPrefs.getCurrentUserId()))
                .first()
                .doOnNext(userMetaData -> mUserMetaData = userMetaData)
                .subscribe(userMetaData -> {
                    Log.d(TAG, "initUserMetaData: " + userMetaData.toString());
                }, Throwable::printStackTrace);
    }

    public Observable<Set<String>> getFavoritesChannels() {
        return Observable.just(mUserMetaData.getFavoriteChannels());
    }

    public Completable addFavoriteChannel(String channelId) {
        mUserMetaData.getFavoriteChannels().add(channelId);
        return saveUserMetaData();
    }

    public Completable removeFavoriteChannel(String channelId) {
        mUserMetaData.getFavoriteChannels().remove(channelId);
        return saveUserMetaData();
    }

    private Completable saveUserMetaData() {
        return mRxPreferences.getString(KEY_USERS_METADATA, "")
                .asObservable()
                .first()
                .map(string -> mGson.fromJson(string, new TypeToken<Set<UserMetaData>>() {
                }.getType()))
                .map(o -> (Set<UserMetaData>) o)
                .map(userMetaDatas -> {
                    if (userMetaDatas == null) {
                        userMetaDatas = new HashSet<>();
                    }
                    userMetaDatas.add(mUserMetaData);
                    return userMetaDatas;
                })
                .map(mGson::toJson)
                .doOnNext(string -> mSharedPreferences.edit().putString(KEY_USERS_METADATA, string).apply())
                .toCompletable();
    }
}

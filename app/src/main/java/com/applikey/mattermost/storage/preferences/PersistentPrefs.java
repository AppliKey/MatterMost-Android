package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.user.UserMetaData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class PersistentPrefs {

    private static final String TAG = "PersistentPrefs";

    private static final String KEY_USERS_METADATA = Constants.PACKAGE_NAME + ".USERS_METADATA";

    private static final String KEY_SERVER_URLS = Constants.PACKAGE_NAME + ".SERVER_URLS";

    private final Gson mGson;
    private final SharedPreferences mSharedPreferences;

    public PersistentPrefs(Context context, final Gson gson) {
        mGson = gson;

        mSharedPreferences = context.getSharedPreferences(Constants.PERSISTENT_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public Single<Set<UserMetaData>> getUsersMetaData() {
        return Single.defer(() -> Single.just(mSharedPreferences.getString(KEY_USERS_METADATA, "")))
                .map(string -> mGson.fromJson(string, new TypeToken<Set<UserMetaData>>() {
                }.getType()))
                .map(o -> (Set<UserMetaData>) o)
                .map(usersMetaData -> usersMetaData == null ? new HashSet<>() : usersMetaData);
    }

    public Completable saveUserMetaData(UserMetaData userMetaData) {
        return getUsersMetaData()
                .doOnSuccess(usersMetaData -> {
                    usersMetaData.remove(userMetaData);
                    usersMetaData.add(userMetaData);
                })
                .map(mGson::toJson)
                .doOnSuccess(string -> mSharedPreferences.edit().putString(KEY_USERS_METADATA, string).apply())
                .toCompletable();
    }

    public Single<Set<String>> getServerUrls() {
        return Single.defer(() -> Single.just(mSharedPreferences.getString(KEY_SERVER_URLS, "")))
                .subscribeOn(Schedulers.io())
                .map(string -> TextUtils.split(string, ","))
                .map(array -> new HashSet<>(Arrays.asList(array)));
    }

    public Completable saveServerUrl(String serverUrl) {
        return getServerUrls()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(urls -> urls.add(serverUrl))
                .map(urls -> TextUtils.join(",", urls))
                .doOnSuccess(array -> mSharedPreferences.edit().putString(KEY_SERVER_URLS, array).apply())
                .toCompletable();
    }
}

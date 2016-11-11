package com.applikey.mattermost.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.user.UserMetaData;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Set;

import rx.Completable;
import rx.Observable;

public class PersistentPrefs {

    private static final String TAG = "PersistentPrefs";

    private static final String KEY_USERS_METADATA = Constants.PACKAGE_NAME + ".USERS_METADATA";

    private final Gson mGson;
    private final SharedPreferences mSharedPreferences;
    private final RxSharedPreferences mRxPreferences;

    public PersistentPrefs(Context context, final Gson gson) {
        mGson = gson;

        mSharedPreferences = context.getSharedPreferences(Constants.PERSISTENT_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        mRxPreferences = RxSharedPreferences.create(mSharedPreferences);
    }

    public Observable<Set<UserMetaData>> getUsersMetaData() {
        return mRxPreferences.getString(KEY_USERS_METADATA, "")
                .asObservable()
                .map(string -> mGson.fromJson(string, new TypeToken<Set<UserMetaData>>() {
                }.getType()))
                .map(o -> (Set<UserMetaData>) o)
                .map(usersMetaData -> usersMetaData == null ? new HashSet<>() : usersMetaData);
    }

    public Completable saveUserMetaData(UserMetaData userMetaData) {
        return getUsersMetaData()
                .first()
                .doOnNext(usersMetaData -> {
                    usersMetaData.remove(userMetaData);
                    usersMetaData.add(userMetaData);
                })
                .map(mGson::toJson)
                .doOnNext(string -> mSharedPreferences.edit().putString(KEY_USERS_METADATA, string).apply())
                .toCompletable();
    }
}

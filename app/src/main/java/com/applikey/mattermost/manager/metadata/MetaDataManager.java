package com.applikey.mattermost.manager.metadata;


import android.support.annotation.WorkerThread;

import com.applikey.mattermost.models.user.UserMetaData;
import com.applikey.mattermost.storage.preferences.PersistentPrefs;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;

public class MetaDataManager {

    private static final String TAG = "MetaDataManager";

    private final Prefs mPrefs;
    private final PersistentPrefs mPersistentPrefs;

    private UserMetaData mUserMetaData;

    private final SerializedSubject<UserMetaData, UserMetaData> mMetaDataSubject;

    public MetaDataManager(final Prefs prefs, final PersistentPrefs persistentPrefs) {
        mPrefs = prefs;
        mPersistentPrefs = persistentPrefs;
        initUserMetaData();

        mMetaDataSubject = new SerializedSubject<>(BehaviorSubject.create());
    }

    private void initUserMetaData() {
        mPersistentPrefs.getUsersMetaData()
                .subscribeOn(Schedulers.io())
                .first()
                .flatMap(Observable::from)
                .filter(userMetadata -> userMetadata.isSame(generateMetaDataKey()))
                .firstOrDefault(new UserMetaData(generateMetaDataKey()))
                .doOnNext(userMetaData -> mUserMetaData = userMetaData)
                .subscribe(userMetaData -> emitMetaData(), Throwable::printStackTrace);
    }

    @WorkerThread
    private void emitMetaData() {
        if (mMetaDataSubject == null) {
            return;
        }
        mMetaDataSubject.onNext(mUserMetaData);
    }

    private String generateMetaDataKey() {
        return mPrefs.getCurrentServerUrl() + mPrefs.getCurrentTeamId() + mPrefs.getCurrentUserId();
    }

    public Observable<UserMetaData> getMetaDataObservable() {
        return mMetaDataSubject;
    }

    public Observable<Set<String>> getFavoriteChannels() {
        return getMetaDataObservable()
                .map(UserMetaData::getFavoriteChannels)
                .subscribeOn(Schedulers.io());
    }

    public Completable setFavoriteChannel(String channelId, boolean favorite) {
        if (favorite) {
            mUserMetaData.getFavoriteChannels().add(channelId);
        } else {
            mUserMetaData.getFavoriteChannels().remove(channelId);
        }
        emitMetaData();
        return mPersistentPrefs.saveUserMetaData(mUserMetaData)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> isFavoriteChannel(String channelId) {
        return getFavoriteChannels()
                .map(ids -> ids.contains(channelId));
    }
}

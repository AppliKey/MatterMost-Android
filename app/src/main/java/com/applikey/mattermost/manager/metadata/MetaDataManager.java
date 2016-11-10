package com.applikey.mattermost.manager.metadata;


import com.applikey.mattermost.models.user.UserMetaData;
import com.applikey.mattermost.storage.preferences.PersistencePrefs;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class MetaDataManager {

    private static final String TAG = "MetaDataManager";

    private final Prefs mPrefs;
    private final PersistencePrefs mPersistencePrefs;

    private UserMetaData mUserMetaData;

    private Subscriber<? super Set<String>> mFavoriteChannelsSubscriber;
    private final Observable<Set<String>> mFavoriteChannelsObservable;

    public MetaDataManager(final Prefs prefs, final PersistencePrefs persistencePrefs) {
        mPrefs = prefs;
        mPersistencePrefs = persistencePrefs;
        initUserMetaData();

        mFavoriteChannelsObservable = Observable.create(subscriber -> {
            mFavoriteChannelsSubscriber = subscriber;
            emitFavoriteChannels();
        });
    }

    private void initUserMetaData() {
        mPersistencePrefs.getUsersMetaData()
                .subscribeOn(Schedulers.io())
                .first()
                .flatMap(Observable::from)
                .filter(userMetadata -> userMetadata.isSame(generateMataDataKey()))
                .firstOrDefault(new UserMetaData(generateMataDataKey()))
                .doOnNext(userMetaData -> mUserMetaData = userMetaData)
                .subscribe(userMetaData -> {
                }, Throwable::printStackTrace);
    }

    private void emitFavoriteChannels() {
        if (mFavoriteChannelsObservable == null) {
            return;
        }
        mFavoriteChannelsSubscriber.onNext(mUserMetaData.getFavoriteChannels());
    }

    private String generateMataDataKey() {
        return mPrefs.getCurrentServerUrl() + mPrefs.getCurrentTeamId() + mPrefs.getCurrentUserId();
    }

    public Observable<Set<String>> getFavoriteChannels() {
        return mFavoriteChannelsObservable
                .subscribeOn(Schedulers.io());
    }

    public Completable setFavoriteChannel(String channelId, boolean favorite) {
        if (favorite) {
            mUserMetaData.getFavoriteChannels().add(channelId);
        } else {
            mUserMetaData.getFavoriteChannels().remove(channelId);
        }
        emitFavoriteChannels();
        return mPersistencePrefs.saveUserMetaData(mUserMetaData)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> isFavoriteChannel(String channelId) {
        return getFavoriteChannels()
                .map(ids -> ids.contains(channelId));
    }
}

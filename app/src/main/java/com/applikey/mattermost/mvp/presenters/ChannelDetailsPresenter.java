package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.manager.metadata.MetaDataManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class ChannelDetailsPresenter extends BasePresenter<ChannelDetailsView>
        implements FavoriteablePresenter {

    private static final String TAG = ChannelDetailsPresenter.class.getSimpleName();

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    Prefs mPrefs;

    @Inject
    MetaDataManager mMetaDataManager;

    private Channel mChannel;

    private boolean mIsFavorite;

    public ChannelDetailsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        mSubscription.add(mChannelStorage.channelById(channelId)
                .doOnNext(channel -> mChannel = channel)
                .doOnNext(channel -> getViewState().showBaseDetails(channel))
                .map(Channel::getUsers)
                .subscribe(users -> getViewState().showMembers(users),
                        mErrorHandler::handleError));

        mMetaDataManager.isFavoriteChannel(channelId)
                .doOnNext(favorite -> mIsFavorite = favorite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favorite -> getViewState().onMakeFavorite(favorite),
                        mErrorHandler::handleError);
    }

    @Override
    public void toggleFavorite() {
        Log.d(TAG, "toggleFavorite: " + !mIsFavorite);
        mSubscription.add(mMetaDataManager.setFavoriteChannel(mChannel.getId(), !mIsFavorite)
                .subscribe());
    }

    public void onEditChannel() {
        getViewState().openEditChannel(mChannel);
    }
}

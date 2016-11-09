package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.channel.MemberInfo;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private Channel mChannel;

    private boolean mIsFavorite;

    public ChannelDetailsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChannelDetailsView view = getViewState();

        mSubscription.add(mChannelStorage.channelById(channelId)
                .doOnNext(channel -> mChannel = channel)
                .subscribe(view::showBaseDetails, mErrorHandler::handleError));

        mChannelStorage.isFavorite(channelId)
                .doOnNext(favorite -> mIsFavorite = favorite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onMakeFavorite, mErrorHandler::handleError);

        mSubscription.add(mApi.getChannelExtra(mPrefs.getCurrentTeamId(), channelId)
                .subscribeOn(Schedulers.io())
                .map(ExtraInfo::getMembers)
                .flatMap(Observable::from)
                .map(MemberInfo::getId)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(ids -> mUserStorage.findUsers(ids))
                .subscribe(view::showMembers, mErrorHandler::handleError));
    }

    @Override
    public void toggleFavorite() {
        Log.d(TAG, "toggleFavorite: " + !mIsFavorite);
        mChannelStorage.setFavorite(mChannel.getId(), !mIsFavorite)
                .subscribe();
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.mvp.views.FindMoreChannelsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class FindMoreChannelsPresenter extends BasePresenter<FindMoreChannelsView> {

    @Inject
    Api mApi;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    ErrorHandler mErrorHandler;

    public FindMoreChannelsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void requestNotJoinedChannels() {
        Timber.d("show loading");
        getViewState().showLoading();
        final Subscription subscription = getNotJoinedChannels()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(mChannelStorage::save)
                .subscribe(this::showResult, this::handleError);
        mSubscription.add(subscription);
    }

    private Single<List<Channel>> getNotJoinedChannels() {
        return mTeamStorage.getTeamId()
                .observeOn(Schedulers.io())
                .flatMap(mApi::getChannelsUserHasNotJoined)
                .map(ChannelResponse::getChannels);
    }

    private void handleError(Throwable e) {
        Timber.d("hide loading");
        getViewState().hideLoading();
        getViewState().showEmptyState();
        mErrorHandler.handleError(e);
    }

    private void showResult(List<Channel> result) {
        Timber.d("show result");
        getViewState().hideLoading();
        getViewState().showNotJoinedChannels(result);
    }
}

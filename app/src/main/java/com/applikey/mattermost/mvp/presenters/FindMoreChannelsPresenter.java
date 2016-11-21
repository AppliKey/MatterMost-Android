package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.FindMoreChannelsView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class FindMoreChannelsPresenter extends BasePresenter<FindMoreChannelsView> {

    @Inject
    Api mApi;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ErrorHandler mErrorHandler;

    public FindMoreChannelsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void requestNotJoinedChannels() {
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .map(Team::getId)
                .observeOn(Schedulers.io())
                .flatMap(id -> mApi.getChannelsUserHasNotJoined(id),
                         (id, channelResponse) -> channelResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelResponse -> {
                }, mErrorHandler::handleError);
        mSubscription.add(subscription);
    }
}

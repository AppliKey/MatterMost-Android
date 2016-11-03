package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.MvpView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Anatoliy Chub
 */

public abstract class SearchPresenter<T extends MvpView> extends BasePresenter<T> {

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    Api mApi;

    protected boolean mChannelsIsFetched = false;

    protected List<Channel> mFetchedChannels = new ArrayList<>();

    public void requestNotJoinedChannels(){
        mSubscription.add(mTeamStorage.getChosenTeam()
                .map(Team::getId)
                .observeOn(Schedulers.io())
                .flatMap(id -> mApi.getChannelsUserHasNotJoined(id),
                        (id, channelResponse) -> channelResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelResponse -> {
                        mFetchedChannels = channelResponse.getChannels();
                        mChannelsIsFetched = true;
                        getData("");
                }, ErrorHandler::handleError));
    }

    public abstract void getData(String text);
}

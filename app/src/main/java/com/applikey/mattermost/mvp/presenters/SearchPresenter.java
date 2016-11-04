package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

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

public abstract class SearchPresenter<T extends MvpView> extends BasePresenter<T> {

    protected boolean mChannelsIsFetched = false;
    protected List<Channel> mFetchedChannels = new ArrayList<>();
    @Inject
    TeamStorage mTeamStorage;
    @Inject
    Api mApi;

    public void requestNotJoinedChannels() {
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

    public List<Channel> addFilterChannels(List<Channel> channels, String text) {
        for (Channel channel : mFetchedChannels) {
            String name = channel.getName();
            if (!TextUtils.isEmpty(name) && name.contains(text)) {
                channels.add(channel);
            }
        }
        return channels;
    }
}

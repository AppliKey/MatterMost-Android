package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.SearchView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

// TODO: Refactor
public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> {

    boolean mChannelsIsFetched = false;

    private List<Channel> mFetchedChannels = new ArrayList<>();

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

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
                }, mErrorHandler::handleError));
    }

    List<Channel> addFilterChannels(List<Channel> channels, String text) {
        for (Channel channel : mFetchedChannels) {
            String name = channel.getName();
            if (!TextUtils.isEmpty(name) && name.contains(text)) {
                channels.add(channel);
            }
        }
        return channels;
    }


    public abstract void getData(String text);
}

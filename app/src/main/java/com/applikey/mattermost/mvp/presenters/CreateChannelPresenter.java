package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class CreateChannelPresenter extends BasePresenter<CreateChannelView> {


    @Inject
    Api mApi;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    UserStorage mUserStorage;


    public CreateChannelPresenter() {
        App.getComponent().inject(this);
    }

    private Observable<Channel> createChannel(ChannelRequest request) {
        return  mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createChannel(team.getId(), request));
    }

    public void getTeamMembersByFilter() {
        Subscription subscription = mUserStorage.listDirectProfiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }



}


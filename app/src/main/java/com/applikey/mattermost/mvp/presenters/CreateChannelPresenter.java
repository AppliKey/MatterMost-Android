package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

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

    private List<User> mInvitedUsers = new ArrayList<>(0);


    public CreateChannelPresenter() {
        App.getComponent().inject(this);
    }

    private Observable<Channel> createChannel(ChannelRequest request) {
        return  mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createChannel(team.getId(), request));
    }


    @Override
    public void onFirstViewAttach() {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .map(this::convertToPendingUsers)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> getViewState().showUsers(results));
        mSubscription.add(subscription);
    }

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users) {
        final List<UserPendingInvitation> pendingInvitations = new ArrayList<>(users.size());
        for (User user : users) {
            pendingInvitations.add(new UserPendingInvitation(user, false));
        }
        return pendingInvitations;
    }

    public void addUser(User user) {
        mInvitedUsers.add(user);
        getViewState().showAddedUsers(mInvitedUsers);
    }
    public void removeUser(User user) {
        mInvitedUsers.remove(user);
        getViewState().showAddedUsers(mInvitedUsers);
    }


}


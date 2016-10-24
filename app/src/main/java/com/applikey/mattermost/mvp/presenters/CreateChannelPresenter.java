package com.applikey.mattermost.mvp.presenters;

import android.content.res.Resources;
import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
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

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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

    @Inject
    Resources mResources;

    private List<User> mInvitedUsers = new ArrayList<>(0);


    public CreateChannelPresenter() {
        App.getComponent().inject(this);
    }

    private void createChannelWithRequest(ChannelRequest request) {
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createChannel(team.getId(), request).subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        channel -> {
                            Timber.d("successfully created channel with name: %s", channel.getName());
                        },
                        Timber::e
                );
        mSubscription.add(subscription);
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


    public void createChannel(String channelName, String channelDescription, boolean isPublicChannel) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showError(mResources.getString(R.string.error_channel_name_empty));
            return;
        }
        final String channelType;
        if (isPublicChannel) {
            channelType = Channel.ChannelType.PUBLIC.getRepresentation();
        } else {
            channelType = Channel.ChannelType.PRIVATE.getRepresentation();
        }
        final ChannelRequest channelRequest = new ChannelRequest(channelName, channelDescription, channelType);
        createChannelWithRequest(channelRequest);
    }
}


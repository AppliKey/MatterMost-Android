package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelPurposeRequest;
import com.applikey.mattermost.models.channel.ChannelTitleRequest;
import com.applikey.mattermost.models.channel.InvitedUsersManager;
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.arellomobile.mvp.InjectViewState;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class EditChannelPresenter extends BaseEditChannelPresenter<EditChannelView> {

    private Channel mChannel;

    public EditChannelPresenter() {
        super();
    }

    @Override
    public void onAllAlreadyInvited(boolean isAllAlreadyInvited) {
    }

    public void getInitialData(String channelId) {
        final Subscription subscription = getUserList()
                .toSortedList()
                .doOnNext(users -> mInvitedUsersManager = new InvitedUsersManager(this, users))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(users -> getViewState().showAllUsers(users))
                .flatMap(users -> mChannelStorage.channelById(channelId))
                .first()
                .doOnNext(channel -> mChannel = channel)
                .doOnNext(channel -> getViewState().showChannelData(channel))
                .flatMap(channel -> mUserStorage.getChannelUsers(channel))
                .doOnNext(users -> mInvitedUsersManager.setAlreadyMemberUsers(users))
                .subscribe(users -> getViewState().showMembers(users),
                        error -> getViewState().showError(mErrorHandler.getErrorMessage(error))
                );
        mSubscription.add(subscription);
    }


    public void updateChannel(String channelName, String channelDescription) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showEmptyChannelNameError(
                    mChannel.getType().equals(Channel.ChannelType.PUBLIC.getRepresentation()));
            return;
        }

        final ChannelTitleRequest channelTitleRequest = new ChannelTitleRequest(mChannel);
        channelTitleRequest.setDisplayName(channelName);
        final ChannelPurposeRequest channelPurposeRequest = new ChannelPurposeRequest(
                mChannel.getId(), channelDescription);
        updateChannel(channelTitleRequest, channelPurposeRequest);
    }

    private void updateChannel(ChannelTitleRequest channelTitleRequest,
            ChannelPurposeRequest channelPurposeRequest) {
        String teamId = mPrefs.getCurrentTeamId();
        String channelId = mChannel.getId();
        mApi.updateChannelTitle(teamId, channelTitleRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.saveChannel(channel))
                .observeOn(Schedulers.io())
                .flatMap(channel -> mApi.updateChannelPurpose(teamId, channelPurposeRequest))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.saveChannel(channel))
                .flatMap(createdChannel -> Observable.from(mInvitedUsersManager.getInvitedUsers()))
                .observeOn(Schedulers.io())
                .flatMap(user -> mApi.addUserToChannel(teamId, channelId,
                        new RequestUserId(user.getId())), (user, membership) -> user)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .map(users -> {
                    users.addAll(mInvitedUsersManager.getAlreadyMemberUsers());
                    return users;
                })
                .doOnNext(users -> mChannelStorage.setUsers(channelId, users))
                .toCompletable()
                .subscribe(() -> getViewState().onChannelUpdated(),
                        error -> getViewState().showError(mErrorHandler.getErrorMessage(error)));
    }
}

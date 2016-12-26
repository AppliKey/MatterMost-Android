package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.AddedUser;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.models.channel.CreatedChannel;
import com.applikey.mattermost.models.channel.InvitedUsersManager;
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.arellomobile.mvp.InjectViewState;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class CreateChannelPresenter extends BaseEditChannelPresenter<CreateChannelView> {

    public CreateChannelPresenter() {
        super();
    }

    @Override
    public void onFirstViewAttach() {
        final Subscription subscription = getUserList()
                .toSortedList()
                .doOnNext(users -> mInvitedUsersManager = new InvitedUsersManager(this, users))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showAllUsers(results),
                        error -> getViewState().showError(mErrorHandler.getErrorMessage(error))
                          );
        mSubscription.add(subscription);
    }

    @Override
    public void onAllAlreadyInvited(boolean isAllAlreadyInvited) {
        getViewState().setButtonAddAllState(isAllAlreadyInvited);
    }

    public void createChannel(String channelName, String channelDescription, boolean isPublicChannel) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showEmptyChannelNameError(isPublicChannel);
            return;
        }

        final String channelType = isPublicChannel
                ? Channel.ChannelType.PUBLIC.getRepresentation()
                : Channel.ChannelType.PRIVATE.getRepresentation();

        final ChannelRequest channelRequest = new ChannelRequest(channelName, channelDescription,
                                                                 channelType);
        createChannelWithRequest(channelRequest);
    }

    private void createChannelWithRequest(ChannelRequest request) {
        final Subscription subscription = mApi.createChannel(mPrefs.getCurrentTeamId(), request)
                .subscribeOn(Schedulers.io())
                .map(channel -> new CreatedChannel(mPrefs.getCurrentTeamId(), channel))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(createdChannel -> {
                    final Channel channel = createdChannel.getChannel();
                    channel.updateLastActivityTime();
                    mChannelStorage.save(channel);
                })
                .observeOn(Schedulers.io())
                .toObservable()
                .flatMap(createdChannel -> Observable.from(mInvitedUsersManager.getInvitedUsers()), AddedUser::new)
                .flatMap(user -> mApi.addUserToChannel(user.getCreatedChannel().getTeamId(),
                                                       user.getCreatedChannel().getChannel().getId(),
                                                       new RequestUserId(user.getUser().getId())).toObservable())
                .toList()
                .first()
                .toCompletable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()-> getViewState().onChannelCreated(),
                           error -> getViewState().showError(mErrorHandler.getErrorMessage(error)));
        mSubscription.add(subscription);
    }
}


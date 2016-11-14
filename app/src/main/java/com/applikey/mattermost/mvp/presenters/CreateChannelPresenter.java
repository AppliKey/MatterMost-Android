package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.channel.AddedUser;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.models.channel.CreatedChannel;
import com.applikey.mattermost.models.channel.InvitedUsersManager;
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.applikey.mattermost.utils.rx.RxUtils.doOnUi;

@InjectViewState
public class CreateChannelPresenter extends BasePresenter<CreateChannelView> implements InvitedUsersManager.OnInvitedListener {

    @Inject
    Api mApi;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ErrorHandler mErrorHandler;

    private InvitedUsersManager mInvitedUsersManager;

    public CreateChannelPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    public void onFirstViewAttach() {
        final Subscription subscription = getUserList()
                .toSortedList()
                .doOnNext(users -> mInvitedUsersManager = new InvitedUsersManager(this, users))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showAllUsers(results),
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }

    private Observable<User> getUserList() {
        return mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> !user.getId().equals(mCurrentUserId));
    }

    public void inviteAll() {
        mInvitedUsersManager.inviteAll();
    }

    public void revertInviteAll() {
        mInvitedUsersManager.revertInvitingAll();
    }

    @Override
    public void onInvited(User user) {
        getViewState().showAddedUser(user);
    }

    @Override
    public void onRevertInvite(User user) {
        getViewState().removeUser(user);
    }

    @Override
    public void onInvitedAll(List<User> users) {
        getViewState().showAddedUsers(users);
    }

    @Override
    public void onRevertedAll(List<User> users) {
        getViewState().showAddedUsers(users);
    }

    public void operateWithUser(User user) {
        mInvitedUsersManager.operateWithUser(user);
    }

    public void createChannel(String channelName, String channelDescription, boolean isPublicChannel) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showEmptyChannelNameError();
            return;
        }

        final String channelType = isPublicChannel
                ? Channel.ChannelType.PUBLIC.getRepresentation()
                : Channel.ChannelType.PRIVATE.getRepresentation();

        final ChannelRequest channelRequest = new ChannelRequest(channelName, channelDescription, channelType);
        createChannelWithRequest(channelRequest);
    }

    private void createChannelWithRequest(ChannelRequest request) {
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .map(Team::getId)
                .first()
                .observeOn(Schedulers.io())
                .flatMap(teamId -> mApi.createChannel(teamId, request), CreatedChannel::new)
                .compose(doOnUi(createdChannel -> mChannelStorage.save(createdChannel.getChannel()), Schedulers.io()))
                .flatMap(createdChannel -> Observable.from(mInvitedUsersManager.getInvitedUsers()), AddedUser::new)
                .flatMap(user -> mApi.addUserToChannel(user.getCreatedChannel().getTeamId(),
                        user.getCreatedChannel().getChannel().getId(),
                        new RequestUserId(user.getUser().getId())))
                .toCompletable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getViewState().onChannelCreated(), error -> getViewState().showError(mErrorHandler.getErrorMessage(error)));
        mSubscription.add(subscription);
    }

    public void filterByFullName(String filter) {
        final List<User> foundedUsers = Stream.of(mInvitedUsersManager.getTeamMembers())
                .filter(user -> user.search(filter))
                .collect(Collectors.toList());
        getViewState().showAllUsers(foundedUsers);
    }

    public List<User> getInvitedUsers() {
        return mInvitedUsersManager.getInvitedUsers();
    }

    public void setAlreadyAddedUsers(List<User> data) {
        if (mInvitedUsersManager != null) {
            mInvitedUsersManager.setAlreadyInvitedUsers(data);
        }
    }

    @Override
    public void onAllAlreadyInvited(boolean isAllAlreadyInvited) {
        getViewState().setButtonAddAllState(isAllAlreadyInvited);
    }
}


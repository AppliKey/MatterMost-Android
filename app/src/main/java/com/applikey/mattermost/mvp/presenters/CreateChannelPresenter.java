package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.AddedUser;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.models.channel.CreatedChannel;
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.team.Team;
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
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static rx.Observable.from;

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

    private List<User> mInvitedUsers = new ArrayList<>();

    public CreateChannelPresenter() {
        App.getComponent().inject(this);
    }

    private void createChannelWithRequest(ChannelRequest request) {
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .observeOn(Schedulers.io())
                .map(Team::getId)
                .first()
                .flatMap(teamId -> mApi.createChannel(teamId, request), (teamId, channel) -> new CreatedChannel(teamId, channel.getId()))
                .flatMap(createdChannel -> from(mInvitedUsers), AddedUser::new)
                .flatMap(user -> mApi.addUserToChannel(user.getCreatedChannel().getTeamId(), user.getCreatedChannel().getChannelId(), new RequestUserId(user.getUser().getId())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        v -> {
                        },
                        error -> Timber.d("empty sequence"),
                        () -> getViewState().successfulClose());
        mSubscription.add(subscription);
    }


    @Override
    public void onFirstViewAttach() {
        final Subscription subscription = mUserStorage.listDirectProfiles(true)
                .compose(this.sortList(User::compareTo))
                .map(users -> convertToPendingUsers(users, false))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showUsers(results),
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }

    private <V> Observable.Transformer<List<V>, List<V>> sortList(Func2<V, V, Integer> sortFunction) {
        return tObservable -> tObservable
                .first()
                .flatMap(Observable::from)
                .toSortedList(sortFunction);
    }

    public void getUsersAndFilterByFullName(String filterString, List<User> alreadyAddedUsers) {
        final Subscription subscription = mUserStorage.listDirectProfiles(false)
                .compose(this.sortList(User::compareTo))
                .map(users -> convertToPendingUsers(users, alreadyAddedUsers))
                .map(users -> filterUserListByFullName(users, filterString))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showUsers(results),
                        Timber::e);
        mSubscription.add(subscription);
    }

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users, boolean invited) {
        return Stream.of(users)
                .map(user -> new UserPendingInvitation(user, invited))
                .collect(Collectors.toList());
    }

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users, List<User> alreadyAddedUsers) {
        final List<UserPendingInvitation> pp = new ArrayList<>(users.size());
        Stream.of(users).forEach(user -> {
            final boolean isAlreadyAdded =
                Stream.of(alreadyAddedUsers).map(user::equals).filter(isAdded -> isAdded).findFirst().orElse(false);
            pp.add(new UserPendingInvitation(user, isAlreadyAdded));
        });
        return pp;
/*        final List<UserPendingInvitation> pendingInvitations = new ArrayList<>(users.size());
        for (int i = 0; i < users.size(); i++) {
            boolean alreadyInvited = false;
            User user = users.get(i);
            for (int j = 0; j < alreadyAddedUsers.size(); j++) {
                if (user.equals(alreadyAddedUsers.get(j))) {
                    alreadyInvited = true;
                    break;
                }
            }
            pendingInvitations.add(new UserPendingInvitation(user, alreadyInvited));
        }
        return pendingInvitations;*/
    }

    public void addUser(User user) {
        mInvitedUsers.add(user);
        setAddAllButtonState();
        getViewState().showAddedUsers(mInvitedUsers);
    }

    public void removeUser(User user) {
        mInvitedUsers.remove(user);
        setAddAllButtonState();
        getViewState().showAddedUsers(mInvitedUsers);
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

    private boolean isUserPassesFilter(User user, String filterString) {
        boolean result = false;
        final String firstName = user.getFirstName();
        final String lastName = user.getLastName();
        final String userEmail = user.getEmail();
        if (firstName.contains(filterString) || lastName.contains(filterString) || userEmail.contains(filterString)) {
            result = true;
        }
        return result;

    }

    private List<UserPendingInvitation> filterUserListByFullName(List<UserPendingInvitation> source, String filter) {
        return Stream.of(source)
                .filter(user -> isUserPassesFilter(user.getUser(), filter))
                .collect(Collectors.toList());
    }

    private void setAddAllButtonState() {
        getViewState().setAddAllButtonEnabled(mInvitedUsers.size() == 0);
    }

    public void addAllUsers() {
        final Subscription subscription = mUserStorage.listDirectProfiles(false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    mInvitedUsers = results;
                    getViewState().addAllUsers(results);
                    setAddAllButtonState();
                }, Timber::e);
        mSubscription.add(subscription);
    }
}


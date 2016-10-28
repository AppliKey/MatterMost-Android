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
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.applikey.mattermost.web.Api;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    private List<User> mInvitedUsers = new ArrayList<>();

    public CreateChannelPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    public void onFirstViewAttach() {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> !user.getId().equals(mCurrentUserId))
                .toSortedList()
                .map(users -> convertToPendingUsers(users, false))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showUsers(results),
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }

    public void addAllUsers() {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    mInvitedUsers = results;
                    getViewState().addAllUsers(results);
                    setAddAllButtonState();
                }, Timber::e);
        mSubscription.add(subscription);
    }

    public void getUsersAndFilterByFullName(String filterString, List<User> alreadyAddedUsers) {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> !user.getId().equals(mCurrentUserId))
                .toSortedList()
                .map(users -> convertToPendingUsers(users, alreadyAddedUsers))
                .map(users -> filterUserListByFullName(users, filterString))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showUsers(results),
                        Timber::e);
        mSubscription.add(subscription);
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

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users, boolean invited) {
        return Stream.of(users)
                .map(user -> new UserPendingInvitation(user, invited))
                .collect(Collectors.toList());
    }

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users, List<User> alreadyAddedUsers) {
        final List<UserPendingInvitation> pendingUsers = new ArrayList<>(users.size());
        Stream.of(users).forEach(user -> {
            final boolean isAlreadyAdded =
                    Stream.of(alreadyAddedUsers)
                            .map(user::equals)
                            .filter(isAdded -> isAdded)
                            .findFirst()
                            .orElse(false);
            pendingUsers.add(new UserPendingInvitation(user, isAlreadyAdded));
        });
        return pendingUsers;
    }

    /**
     * How it works:
     * <li>1. All team members are fetched from data source</li>
     * <li>2. Fetched members are sorted by comparator {@link User#compareTo(User)}</li>
     * <li>3. User list is iterated and filtered by comparing ids with every id in
     * <code>addedUsersIds</code>. Thus we get the list of already added users</li>
     * <li>4. The list of already added users is shown in
     * {@link AddedPeopleLayout}</li>
     * <li>5. The initial list of users is converted to {@link UserPendingInvitation}</li>
     * <li>6. The {@link UserPendingInvitation} list is shown in RecyclerView</li>
     *
     * @param addedUsersIds the list of already added users' ids
     */
    public void showAlreadyAddedUsers(List<String> addedUsersIds) {

        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()                                                               //      Start of Section 1
                .flatMap(Observable::from)                                             //
                .filter(user -> !user.getId().equals(mCurrentUserId))                  //      End of Section 1
                .toSortedList()                                                        //      Section 2
                .map(users -> {                                                        //      Start of Section 3
                    final List<User> alreadyAddedUsers = Stream.of(users)              //
                            .filter(user ->                                            //
                                    Stream.of(addedUsersIds)                           //
                                            .anyMatch(id -> user.getId().equals(id)))  //
                            .collect(Collectors.toList());                             //      End of Section 3
                    getViewState().showAddedUsers(alreadyAddedUsers);                  //      Section 4
                    return convertToPendingUsers(users, alreadyAddedUsers);            //      Section 5
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> getViewState().showUsers(results),                  //      Section 6
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }
}


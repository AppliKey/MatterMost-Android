package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class AddedMembersPresenter extends BasePresenter<AddedMembersView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    ErrorHandler mErrorHandler;

    private List<String> mAddedUsersIds;
    private List<User> mResultingList = new ArrayList<>();

    public AddedMembersPresenter() {
        App.getComponent().inject(this);
    }

    public void getUsersByIds(List<String> usersIds) {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> Stream.of(usersIds).anyMatch(id -> user.getId().equals(id)))
                .toSortedList()
                .doOnNext(users -> mResultingList = users)
                .doOnNext(users -> mAddedUsersIds = Stream.of(users)
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .map(users -> Stream.of(users)
                        .map(user -> new UserPendingInvitation(user, true))
                        .collect(Collectors.toList()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> getViewState().showAddedMembers(users),
                        mErrorHandler::handleError
                );
        mSubscription.add(subscription);
    }

    public void addUser(User user) {
        mResultingList.add(user);
    }

    public void removeUser(User user) {
        mResultingList.remove(user);
    }

    public void getUsersAndFilterByFullName(String filter) {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> Stream.of(mAddedUsersIds).anyMatch(id -> user.getId().equals(id)))
                .filter(user -> user.search(filter))
                .toSortedList()
                .map(users -> convertToPendingUsers(users, mResultingList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> getViewState().showAddedMembers(users),
                        mErrorHandler::handleError
                );
        mSubscription.add(subscription);
    }

    public List<User> getResultingList() {
        return mResultingList;
    }

    private List<UserPendingInvitation> convertToPendingUsers(List<User> users,
            List<User> alreadyAddedUsers) {
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
}

package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@InjectViewState
public class AddedMembersPresenter extends BasePresenter<AddedMembersView> {

    @Inject
    UserStorage mUserStorage;

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
                .doOnNext(users -> mAddedUsersIds = Stream.of(users).map(User::getId).collect(Collectors.toList()))
                .map(users -> Stream.of(users).map(user -> new UserPendingInvitation(user, true)).collect(Collectors.toList()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> getViewState().showAddedMembers(users),
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }

    public void addUser(User user) {
        mResultingList.add(user);
        Timber.d("Resulting users");
        for (User user1 : mResultingList) {
            Timber.d("%s", user1);
        }
    }

    public void removeUser(User user) {
        mResultingList.remove(user);
        Timber.d("Resulting users");
        for (User user1 : mResultingList) {
            Timber.d("%s", user1);
        }
    }

    public ArrayList<String> getIds() {
        return (ArrayList<String>) mAddedUsersIds;
    }

    public void getUsersAndFilterByFullName(String filter) {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> Stream.of(mAddedUsersIds).anyMatch(id -> user.getId().equals(id)))
                .filter(user -> isUserPassesFilter(user, filter))
                .toSortedList()
                .map(users -> convertToPendingUsers(users, mResultingList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> getViewState().showAddedMembers(users),
                        error -> Timber.e("", error)
                );
        mSubscription.add(subscription);
    }

    public List<User> getResultingList() {
        return mResultingList;
    }


    private boolean isUserPassesFilter(User user, String filterString) {
        if (TextUtils.isEmpty(filterString)) {
            return true;
        }
        boolean result = false;
        final String firstName = user.getFirstName();
        final String lastName = user.getLastName();
        final String userEmail = user.getEmail();
        if (firstName.contains(filterString) || lastName.contains(filterString) || userEmail.contains(filterString)) {
            result = true;
        }
        return result;

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
}

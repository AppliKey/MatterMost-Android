package com.applikey.mattermost.mvp.presenters;

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

    public AddedMembersPresenter() {
        App.getComponent().inject(this);
    }

    public void getUsersByIds(List<String> usersIds) {
        final Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> Stream.of(usersIds).anyMatch(id -> user.getId().equals(id)))
                .toSortedList()
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
        mAddedUsersIds.add(user.getId());
    }

    public void removeUser(User user) {
        mAddedUsersIds.remove(user.getId());
    }

    public ArrayList<String> getIds() {
        return (ArrayList<String>) mAddedUsersIds;
    }
}

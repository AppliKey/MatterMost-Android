package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class AddedMembersPresenter extends BasePresenter<AddedMembersView> {

    @Inject
    UserStorage mUserStorage;

    public AddedMembersPresenter() {
        App.getComponent().inject(this);
    }

    public void getUsersByIds(List<String> usersIds) {
        Subscription subscription = mUserStorage.listDirectProfiles()
                .first()
                .flatMap(Observable::from)
                .filter(user -> Stream.of(usersIds).anyMatch(id -> user.getId().equals(id)))
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> getViewState().showAddedMembers(users)
                );

    }
}

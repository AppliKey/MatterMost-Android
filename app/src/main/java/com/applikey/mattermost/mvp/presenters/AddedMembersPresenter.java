package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class AddedMembersPresenter extends BasePresenter<AddedMembersView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    ErrorHandler mErrorHandler;

    private final List<String> mAlreadyAddedUsersIds;
    private final boolean mEditable;

    private List<User> mAlreadyAddedUsers;
    private List<User> mPendingUsers;

    public AddedMembersPresenter(List<String> alreadyAddedUsers, boolean editable) {
        App.getUserComponent().inject(this);
        mEditable = editable;
        mAlreadyAddedUsersIds = alreadyAddedUsers;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        setData(mAlreadyAddedUsersIds);
        getViewState().initList(mEditable);
    }

    public List<User> getInvitedUsers() {
        return mAlreadyAddedUsers;
    }

    public void filterByFullName(String filter) {
        final List<User> filteredUsers = Stream.of(mPendingUsers)
                .filter(user -> user.search(filter))
                .collect(Collectors.toList());
        getViewState().showUsers(filteredUsers);
        if (filteredUsers.isEmpty()) {
            getViewState().showEmptyState();
        }
    }

    public void handleUser(User user) {
        if (mAlreadyAddedUsers.contains(user)) {
            removeUser(user);
        } else {
            addUser(user);
        }
    }

    private void setData(List<String> alreadyAddedUsers) {
        mUserStorage.findUsers(alreadyAddedUsers)
                .compose(bindToLifecycle())
                .doOnNext(Collections::sort)
                .doOnNext(users -> {
                    mAlreadyAddedUsers = users;
                    mPendingUsers = new ArrayList<>(mAlreadyAddedUsers);
                })
                .subscribe(users -> {
                    getViewState().showUsers(mAlreadyAddedUsers);
                    getViewState().showAddedMembers(mAlreadyAddedUsers);
                }, mErrorHandler::handleError);
    }

    private void addUser(User user) {
        mAlreadyAddedUsers.add(user);
        getViewState().addInvitedUser(user);
    }

    private void removeUser(User user) {
        mAlreadyAddedUsers.remove(user);
        getViewState().removeInvitedUser(user);
    }
}

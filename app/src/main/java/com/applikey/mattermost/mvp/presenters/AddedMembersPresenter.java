package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class AddedMembersPresenter extends BasePresenter<AddedMembersView> {

    @Inject
    UserStorage mUserStorage;

    private List<User> mAlreadyAddedUsers;

    public AddedMembersPresenter() {
        App.getComponent().inject(this);
    }

    public void setData(List<User> alreadyAddedUsers) {
        mAlreadyAddedUsers = alreadyAddedUsers;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showAddedMembers(mAlreadyAddedUsers);
    }

    private void addUser(User user) {
        mAlreadyAddedUsers.add(user);
    }

    private void removeUser(User user) {
        mAlreadyAddedUsers.remove(user);
    }

    public List<User> getInvitedUsers() {
        return mAlreadyAddedUsers;
    }

    public void filterByFullName(String filter) {
        final List<User> filteredUsers = Stream.of(mAlreadyAddedUsers)
                .filter(user -> user.search(filter))
                .collect(Collectors.toList());
        getViewState().showAddedMembers(filteredUsers);
    }

    public void handleUser(User user) {
        if (mAlreadyAddedUsers.contains(user)) {
            removeUser(user);
        } else {
            addUser(user);
        }
    }
}

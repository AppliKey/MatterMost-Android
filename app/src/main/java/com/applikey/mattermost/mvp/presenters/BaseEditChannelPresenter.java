package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.channel.InvitedUsersManager;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.BaseEditChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public abstract class BaseEditChannelPresenter<V extends BaseEditChannelView> extends BasePresenter<V>
        implements InvitedUsersManager.OnInvitedListener {

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

    @Inject
    Prefs mPrefs;

    InvitedUsersManager mInvitedUsersManager;

    public BaseEditChannelPresenter() {
        App.getUserComponent().inject((BaseEditChannelPresenter<BaseEditChannelView>) this);
    }

    protected Observable<User> getUserList() {
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

}

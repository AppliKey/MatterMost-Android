package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;

import java.util.List;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

public interface BaseEditChannelView extends MvpView {
    void showAddedUser(User user);
    void removeUser(User user);
    void showAllUsers(List<User> allUsers);
    void showEmptyChannelNameError();
    void showAddedUsers(List<User> users);
    void setButtonAddAllState(boolean isAllAlreadyInvited);
    void showError(String error);
    void showEmptyState(boolean isEmpty);
}

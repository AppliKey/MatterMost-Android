package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

public interface BaseEditChannelView extends MvpView {
    void showAddedUser(User user);
    void removeUser(User user);
    void showAllUsers(List<User> allUsers);
    void showEmptyChannelNameError();
    void showAddedUsers(List<User> users);
    void setButtonAddAllState(boolean isAllAlreadyInvited);
    @StateStrategyType(SkipStrategy.class)
    void showError(String error);
    void showEmptyState(boolean isEmpty);
}

package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface CreateChannelView extends MvpView {
    void showAddedUser(User user);
    void removeUser(User user);
    void showAllUsers(List<User> allUsers);
    void showEmptyChannelNameError();
    void onChannelCreated();
    void showAddedUsers(List<User> users);
    void setButtonAddAllState(boolean isAllAlreadyInvited);
}

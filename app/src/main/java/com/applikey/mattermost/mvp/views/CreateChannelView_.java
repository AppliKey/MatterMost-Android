package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface CreateChannelView_ extends MvpView {
    void showAddedUser(User user);
    void removeUser(User user);
    void showAllUsers(List<User> allUsers);
    void showEmptyChannelNameError();
    void successfulClose();
    void showAddedUsers(List<User> users);
}

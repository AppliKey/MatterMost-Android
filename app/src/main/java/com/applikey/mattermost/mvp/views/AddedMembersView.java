package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface AddedMembersView extends MvpView {
    void showUsers(List<User> users);
    void showAddedMembers(List<User> users);
    void addInvitedUser(User user);
    void removeInvitedUser(User user);
}

package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface AddedMembersView extends MvpView {

    void showUsers(List<User> users);

    void showAddedMembers(List<User> users);

    void addInvitedUser(User user);

    void removeInvitedUser(User user);

    void showEmptyState();

    void initList(boolean editable);
}

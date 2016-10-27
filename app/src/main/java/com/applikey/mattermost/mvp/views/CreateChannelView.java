package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(AddToEndStrategy.class)
public interface CreateChannelView extends BaseView {

    void showUsers(List<UserPendingInvitation> results);

    void showAddedUsers(List<User> users);

    void showError(String string);

    void setAddAllButtonEnabled(boolean enabled);

    void successfulClose();

    void addAllUsers(List<User> results);
}

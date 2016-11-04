package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface CreateChannelView extends MvpView {

    void showUsers(List<UserPendingInvitation> results);

    void showAddedUsers(List<User> users);

    void showEmptyChannelNameError();

/*    void setAddAllButtonEnabled(boolean enabled);*/

    void successfulClose();

    void addAllUsers(List<User> results);

    void setAddAllButtonState(boolean isNeedToCancel);
}

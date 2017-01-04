package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = OneExecutionStateStrategy.class)
public interface NavigationView extends MvpView {

    void startChannelCreating();

    @StateStrategyType(value = AddToEndSingleStrategy.class)
    void setUserInfo(User user);

    void setTeamName(String teamName);

    void findMoreChannels();

    void startInviteNewMember();

    void startSettings();
}

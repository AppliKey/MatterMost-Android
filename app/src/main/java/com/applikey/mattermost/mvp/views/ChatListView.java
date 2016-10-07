package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatListView extends MvpView {

    void displayInitialData(List<Channel> channels);
}

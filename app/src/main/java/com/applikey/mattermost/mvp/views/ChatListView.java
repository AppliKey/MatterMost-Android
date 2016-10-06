package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatListView extends MvpView {

    void displayInitialData(ChannelsWithMetadata channelsWithMetadata);
}

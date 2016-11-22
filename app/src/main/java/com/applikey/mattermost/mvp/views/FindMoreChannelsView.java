package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(SingleStateStrategy.class)
public interface FindMoreChannelsView extends MvpView {
    void showNotJoinedChannels(List<Channel> notJoinedChannels);
    void showEmptyState();
    void hideEmptyState();
}

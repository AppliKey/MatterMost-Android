package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface FindMoreChannelsView extends MvpView {

    void showNotJoinedChannels(List<Channel> notJoinedChannels);

    void showLoading();

    void hideLoading();

    void showEmptyState();

    void hideEmptyState();
}

package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface SearchView extends MvpView {

    void setEmptyState(boolean isEmpty);

    @StateStrategyType(value = SkipStrategy.class)
    void startChatView(Channel channel);

    void showLoading(boolean show);

}

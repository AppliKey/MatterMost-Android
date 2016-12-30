package com.applikey.mattermost.mvp.views;

import android.support.v4.app.Fragment;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface ChatListScreenView extends MvpView {

    void setToolbarTitle(String title);

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void onChannelLoaded(Channel channel);

    /**
     * Platform-specific. We call it explicitly to separate this logic from logout.
     */
    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void stopWebSocketService();

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void initViewPager(List<Fragment> pages);
}

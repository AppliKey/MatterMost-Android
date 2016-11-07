package com.applikey.mattermost.mvp.views;

import android.support.v4.app.Fragment;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface ChatListScreenView extends MvpView {

    void setToolbarTitle(String title);

    @StateStrategyType(value = SkipStrategy.class)
    void onChannelLoaded(Channel channel);

    /**
     * Platform-specific. We call it explicitly to separate this logic from logout.
     */
    void stopWebSocketService();

    @StateStrategyType(value = SkipStrategy.class)
    void initViewPager(List<Fragment> pages);
}

package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndStrategy.class)
public interface ChatListScreenView extends MvpView {

    void setToolbarTitle(String title);

    void logout();

    /**
     * Platform-specific. We call it explicitly to separate this logic from logout.
     */
    void stopWebSocketService();
}

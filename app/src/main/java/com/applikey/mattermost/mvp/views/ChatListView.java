package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.realm.RealmResults;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface ChatListView extends MvpView {

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void displayInitialData(RealmResults<Channel> channels);

    void showUnreadIndicator(boolean showIndicator);

    void displayEmptyState(boolean visible);
}

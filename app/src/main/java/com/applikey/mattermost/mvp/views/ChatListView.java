package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.realm.RealmResults;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatListView extends MvpView {

    void displayInitialData(RealmResults<Channel> channels);

    void showUnreadIndicator(boolean showIndicator);
}

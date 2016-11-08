package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface SearchChannelView extends MvpView {

    void displayData(List<SearchItem> searchItems);

    @StateStrategyType(value = SkipStrategy.class)
    void startChatView(Channel channel);

    void showLoading(boolean show);

    void clearData();

}

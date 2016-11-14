package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.SearchItem;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface SearchAllView extends SearchView {

    void displayData(List<SearchItem> items);

    @StateStrategyType(value = SkipStrategy.class)
    void startChatActivity(SearchItem item);

    void showLoading(boolean show);

    void clearData();

}

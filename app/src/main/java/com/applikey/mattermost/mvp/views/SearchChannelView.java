package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.SearchItem;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface SearchChannelView extends SearchView  {

    void displayData(List<SearchItem> searchItems);

    void showLoading(boolean show);

    void clearData();

}

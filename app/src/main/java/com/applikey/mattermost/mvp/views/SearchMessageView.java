package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.post.Post;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SingleStateStrategy.class)
public interface SearchMessageView extends SearchView  {

    void displayData(List<SearchItem> searchItems);

    @StateStrategyType(value = SkipStrategy.class)
    void startChatView(Post post);

    void showLoading(boolean show);

    void clearData();

}

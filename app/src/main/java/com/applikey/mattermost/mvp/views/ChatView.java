package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.Post;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatView extends MvpView {

    void displayData(List<Post> posts);

    void onDataFetched();

    void onFailure(Throwable cause);
}

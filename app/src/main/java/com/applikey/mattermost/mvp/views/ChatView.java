package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.PostDto;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatView extends MvpView {

    void displayData(List<PostDto> posts);

    void onDataFetched();

    void onPostDeleted(String postId);

    void onFailure(Throwable cause);
}

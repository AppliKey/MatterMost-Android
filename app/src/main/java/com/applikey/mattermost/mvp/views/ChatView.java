package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.realm.RealmResults;

@StateStrategyType(value = SkipStrategy.class)
public interface ChatView extends MvpView {

    void onRealmAttached(RealmResults<Post> posts);

    void onDataFetched();

    void showProgress(boolean enabled);

    void onFailure(Throwable cause);
}

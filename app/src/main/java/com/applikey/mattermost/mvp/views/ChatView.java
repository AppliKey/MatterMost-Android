package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.realm.RealmResults;

@StateStrategyType(SkipStrategy.class)
public interface ChatView extends MvpView {

    void onDataReady(RealmResults<Post> posts);

    void onDataFetched();

    void showProgress();

    void hideProgress();

    void onMessageSent(long createdAt);

    void onChannelJoined();

    void openChannelDetails(Channel channel);

    void openUserProfile(User user);

    void showTitle(String title);

    void showEmpty(boolean show);

    void showJoiningInterface(String channelName);
}

package com.applikey.mattermost.mvp.views;

import android.app.DownloadManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.realm.RealmResults;

@StateStrategyType(SkipStrategy.class)
public interface ChatView extends MvpView {

    void onDataReady(RealmResults<Post> posts, boolean listenUpdates);

    void subscribeForMessageChanges();

    void showProgress();

    void hideProgress();

    void onMessageSent(long createdAt);

    void clearMessageInput();

    void onChannelJoined();

    void openChannelDetails(Channel channel);

    void openUserProfile(User user);

    void showTitle(String title);

    void showEmpty(boolean show);

    void showJoiningInterface(String channelName);

    void showLoading(boolean show);

    void downloadFile(DownloadManager.Request downloadRequest, String fileName);
}

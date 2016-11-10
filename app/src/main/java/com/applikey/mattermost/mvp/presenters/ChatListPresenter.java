package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.models.channel.Channel;

import io.realm.RealmResults;
import rx.Observable;

public interface ChatListPresenter {

    Observable<RealmResults<Channel>> getInitData();

    void displayData();

    void getLastPost(Channel channel);
}

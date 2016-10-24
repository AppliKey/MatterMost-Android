package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.InjectViewState;

import io.realm.RealmResults;
import rx.Observable;

@InjectViewState
public class ChannelListPresenter extends BaseChatListPresenter {

    public ChannelListPresenter() {
        super();
    }

    @Override
    protected Observable<RealmResults<Channel>> getInitData() {
        return mChannelStorage.listOpen();
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.InjectViewState;

import io.realm.RealmResults;
import rx.Observable;

@InjectViewState
public class GroupListPresenter extends BaseChatListPresenter {

    public GroupListPresenter() {
        super();
    }

    @Override
    protected Observable<RealmResults<Channel>> getInitData() {
        return mChannelStorage.listClosed();
    }
}

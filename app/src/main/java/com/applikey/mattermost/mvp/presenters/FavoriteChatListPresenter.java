package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.arellomobile.mvp.InjectViewState;

import io.realm.RealmResults;
import rx.Observable;

@InjectViewState
public class FavoriteChatListPresenter extends BaseChatListPresenter {

    public FavoriteChatListPresenter() {
        super();
        App.getUserComponent().inject(this);
    }

    @Override
    public Observable<RealmResults<Channel>> getInitData() {
        return mChannelStorage.listFavorite();
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;

import javax.inject.Inject;

public abstract class BaseChatListPresenter extends BasePresenter<ChatListView>
        implements ChatListPresenter {

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    /* package */ BaseChatListPresenter() {
        App.getComponent().inject(this);
    }
}


package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.web.Api;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

@InjectViewState
public class ChatPresenter extends BasePresenter<ChatView> {

    @Inject
    PostStorage mPostStorage;

    @Inject
    Api mApi;

    public ChatPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChatView view = getViewState();
        mPostStorage.listByChannel(channelId).subscribe(view::displayData, view::onFailure);
    }

    public void fetchData(int offset) {

    }
}

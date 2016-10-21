package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

@InjectViewState
public class DirectChatListPresenter extends BaseChatListPresenter {

    @Inject
    UserStorage mUserStorage;

    public DirectChatListPresenter() {
        super();
        App.getComponent().inject(this);
    }

    @Override
    protected void getInitData() {
        final ChatListView view = getViewState();
        if (view == null) {
            return;
        }
        mSubscription.add(mChannelStorage.listDirect()
                .subscribe(view::displayInitialData));
    }
}
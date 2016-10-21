package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.mvp.views.ChatListView;
import com.arellomobile.mvp.InjectViewState;

@InjectViewState
public class GroupListPresenter extends BaseChatListPresenter {

    public GroupListPresenter() {
        super();
    }

    @Override
    protected void getInitData() {
        final ChatListView view = getViewState();
        if(view == null){
            return;
        }
        mSubscription.add(mChannelStorage.listClosed()
                .subscribe(view::displayInitialData));
    }
}

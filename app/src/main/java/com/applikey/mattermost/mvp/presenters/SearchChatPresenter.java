package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.events.SearchAllTextChanged;
import com.applikey.mattermost.events.SearchChannelTextChanged;
import com.applikey.mattermost.events.SearchUserTextChanged;
import com.applikey.mattermost.mvp.views.SearchChatView;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

@InjectViewState
public class SearchChatPresenter extends BasePresenter<SearchChatView> {

    @Inject
    EventBus mBus;

    public SearchChatPresenter() {
        App.getComponent().inject(this);
    }

    public void handleUserTextChanges(String text) {
        mBus.post(new SearchUserTextChanged(text));
    }

    public void handleChannelTextChanges(String text) {
        mBus.post(new SearchChannelTextChanged(text));
    }

    public void handleAllTextChanges(String text) {
        mBus.post(new SearchAllTextChanged(text));
    }

}

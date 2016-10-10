package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class UnreadChatListPresenter extends BaseChatListPresenter {

    @Inject
    UserStorage mUserStorage;

    public UnreadChatListPresenter() {
        super();
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChatListView view = getViewState();
        mSubscription.add(
                mChannelStorage.listUnread()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class DirectChatListPresenter extends BaseChatListPresenter {

    @Inject
    UserStorage mUserStorage;

    public DirectChatListPresenter() {
        super();
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChatListView view = getView();
        mSubscription.add(
                Observable.zip(
                        mChannelStorage.listDirect(),
                        mChannelStorage.listMembership(),
                        mUserStorage.listDirectProfiles(),
                        this::transform)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }
}

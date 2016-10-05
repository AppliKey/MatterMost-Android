package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.web.ErrorHandler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class GroupListPresenter extends BaseChatListPresenter {

    public GroupListPresenter() {
        super();
    }

    @Override
    public void getInitialData() {
        final ChatListView view = getView();
        mSubscription.add(
                Observable.zip(
                        mChannelStorage.listClosed(),
                        mChannelStorage.listMembership(),
                        this::transform)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }
}

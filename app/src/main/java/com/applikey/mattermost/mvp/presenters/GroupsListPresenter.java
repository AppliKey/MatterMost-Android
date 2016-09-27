package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.mvp.views.ChannelsListView;
import com.applikey.mattermost.web.ErrorHandler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class GroupsListPresenter extends BaseChatListPresenter {

    public GroupsListPresenter() {
        super();
    }

    @Override
    public void getInitialData() {
        final ChannelsListView view = getView();
        getSubscription().add(
                Observable.zip(
                        mChannelStorage.listClosed(),
                        mChannelStorage.listMembership(),
                        this::transform)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }
}

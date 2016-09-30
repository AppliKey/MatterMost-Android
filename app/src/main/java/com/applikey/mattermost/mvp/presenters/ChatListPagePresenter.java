package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.applikey.mattermost.storage.db.TeamStorage;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class ChatListPagePresenter extends SingleViewPresenter<ChatListScreenView> {

    @Inject
    TeamStorage mTeamStorage;

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    public ChatListPagePresenter() {
        App.getComponent().inject(this);
    }

    public void applyInitialViewState() {
        mSubscription.add(mTeamStorage.getChosenTeam().subscribe(team -> {
            getView().setToolbarTitle(team.getDisplayName());
        }));
    }

    public void unSubscribe() {
        mSubscription.clear();
    }
}

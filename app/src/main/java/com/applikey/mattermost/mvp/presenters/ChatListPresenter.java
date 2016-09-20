package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.TeamStorage;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class ChatListPresenter extends SingleViewPresenter<ChatListView> {

    @Inject
    TeamStorage mTeamStorage;

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    public ChatListPresenter() {
        App.getComponent().inject(this);
    }

    public void applyInitialViewState() {
        mSubscription.add(mTeamStorage.getChosenTeam().subscribe(team -> {
            getView().setToolbarTitle(team.getDisplayName());
        }));
    }

    public void unSubscribe() {
        mSubscription.unsubscribe();
    }
}

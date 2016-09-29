package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.applikey.mattermost.storage.db.TeamStorage;

import javax.inject.Inject;

public class ChatListScreenPresenter extends SingleViewPresenter<ChatListScreenView> {

    @Inject
    TeamStorage mTeamStorage;

    public ChatListScreenPresenter() {
        App.getComponent().inject(this);
    }

    public void applyInitialViewState() {
        mSubscription.add(mTeamStorage.getChosenTeam().subscribe(team -> {
            getView().setToolbarTitle(team.getDisplayName());
        }));
    }
}

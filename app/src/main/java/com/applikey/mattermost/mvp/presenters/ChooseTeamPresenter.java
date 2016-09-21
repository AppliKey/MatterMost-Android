package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.ChooseTeamView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class ChooseTeamPresenter extends SingleViewPresenter<ChooseTeamView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage mTeamStorage;

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    public ChooseTeamPresenter() {
        App.getComponent().inject(this);

    }

    public void getInitialData() {
        final ChooseTeamView view = getView();
        mSubscription.add(mTeamStorage.listAll()
                .subscribe(view::displayTeams, throwable -> {
                    ErrorHandler.handleError(throwable);
                    getView().onFailure(throwable.getMessage());
                }));
    }

    public void chooseTeam(Team team) {
        mTeamStorage.setChosenTeam(team);
        getView().onTeamChosen();
    }

    public void unSubscribe() {
        mSubscription.unsubscribe();
    }
}

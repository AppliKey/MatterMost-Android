package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.ChooseTeamView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;

@InjectViewState
public class ChooseTeamPresenter extends BasePresenter<ChooseTeamView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ImagePathHelper mImagePathHelper;

    @Inject
    Lazy<ErrorHandler> mErrorHandler;

    public ChooseTeamPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChooseTeamView view = getViewState();
        mSubscription.add(mTeamStorage.listAll()
                .subscribe(view::displayTeams, throwable -> {
                    mErrorHandler.get().handleError(throwable);
                    getViewState().onFailure(throwable.getMessage());
                }));
    }

    public void chooseTeam(Team team) {
        mTeamStorage.setChosenTeam(team);
        mPrefs.setCurrentTeamrId(team.getId());
        final ChooseTeamView view = getViewState();
        view.onTeamChosen();
    }
}

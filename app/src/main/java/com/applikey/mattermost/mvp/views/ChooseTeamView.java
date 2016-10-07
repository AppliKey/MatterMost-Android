package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.team.Team;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = SkipStrategy.class)
public interface ChooseTeamView extends MvpView {

    void displayTeams(List<Team> teams);

    void onTeamChosen();

    void onFailure(String message);
}

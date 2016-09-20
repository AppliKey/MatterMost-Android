package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.team.Team;
import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface ChooseTeamView extends MvpView {

    void displayTeams(List<Team> teams);

    void onTeamChosen();

    void onFailure(String message);
}

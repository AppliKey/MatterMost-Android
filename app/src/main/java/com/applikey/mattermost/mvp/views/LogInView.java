package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.team.Team;
import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface LogInView extends MvpView {

    void displayTeams(List<Team> teams);

    void showLoading();

    void hideLoading();

    void onSuccessfulAuth();

    void onUnsuccessfulAuth(String message);
}

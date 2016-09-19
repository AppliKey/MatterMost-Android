package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.team.Team;
import com.arellomobile.mvp.MvpView;

import java.util.Map;

public interface LogInView extends MvpView {

    void showLoading();

    void hideLoading();

    void onSuccessfulAuth();

    void onUnsuccessfulAuth(String message);

    void onTeamsRetrieved(Map<String, Team> teams);

    void onTeamsReceiveFailed(Throwable cause);
}

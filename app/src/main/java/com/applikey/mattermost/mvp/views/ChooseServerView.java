package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.team.Team;
import com.arellomobile.mvp.MvpView;

import java.util.Map;

public interface ChooseServerView extends MvpView {

    void showValidationError();

    void onTeamsRetrieved(Map<String, Team> teams);

    void onTeamsReceiveFailed(Throwable cause);
}

package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface InviteNewMemberView extends MvpView {

    void showEmptyEmailError();

    void showInvalidEmailError();

    void onSuccessfulInvitationSent();

    void onHttpError();
}

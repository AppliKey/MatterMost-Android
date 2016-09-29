package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface ChooseServerView extends MvpView {

    void showValidationError();

    void onValidServerChosen();
}

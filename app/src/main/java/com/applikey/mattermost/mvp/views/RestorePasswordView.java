package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface RestorePasswordView extends MvpView {

    void onPasswordRestoreSent();

    void onFailure(String message);
}

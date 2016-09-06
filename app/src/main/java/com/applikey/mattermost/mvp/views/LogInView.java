package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface LogInView extends MvpView {

    void showLoading();

    void hideLoading();

    void onSuccessfulAuth();

    void onUnsuccessfulAuth(Throwable throwable);
}

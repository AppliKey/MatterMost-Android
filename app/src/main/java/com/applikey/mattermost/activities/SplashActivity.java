package com.applikey.mattermost.activities;

import android.content.Intent;
import android.os.Bundle;

import com.applikey.mattermost.mvp.presenters.SplashPresenter;
import com.applikey.mattermost.mvp.views.SplashView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class SplashActivity extends BaseMvpActivity implements SplashView {

    @InjectPresenter
    SplashPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.isSessionExist();
    }

    @Override
    public void isSessionExist(boolean exist) {
        Intent intent = new Intent(this, exist ? ChatListActivity.class : ChooseServerActivity.class);
        startActivity(intent);
    }
}


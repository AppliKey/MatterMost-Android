package com.applikey.mattermost.activities;

import android.content.Intent;
import android.os.Bundle;

import com.applikey.mattermost.gcm.RegistrationIntentService;
import com.applikey.mattermost.mvp.presenters.SplashPresenter;
import com.applikey.mattermost.mvp.views.SplashView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class SplashActivity extends BaseMvpActivity implements SplashView {

    @InjectPresenter
    SplashPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        mPresenter.isSessionExist();
    }

    @Override
    public void isSessionExist(boolean exist) {
        final Intent intent = new Intent(this, exist
                ? ChatListActivity.class : ChooseServerActivity.class);
        startActivity(intent);
    }
}

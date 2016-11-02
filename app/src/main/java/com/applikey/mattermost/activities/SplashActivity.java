package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.gcm.RegistrationIntentService;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.applikey.mattermost.mvp.presenters.SplashPresenter;
import com.applikey.mattermost.mvp.views.SplashView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class SplashActivity extends BaseMvpActivity implements SplashView {

    @InjectPresenter
    SplashPresenter mPresenter;

    public static Intent getIntent(Context context, Bundle bundle) {
        final Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(NotificationManager.NOTIFICATION_BUNDLE_KEY, bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(RegistrationIntentService.getIntent(this));

        mPresenter.isSessionExist();
    }

    @Override
    public void isSessionExist(boolean exist) {
        final Intent intent;
        if (!exist) {
            intent = new Intent(this, ChooseServerActivity.class);
        } else {
            intent = ChatListActivity.getIntent(this,
                    getIntent().getBundleExtra(NotificationManager.NOTIFICATION_BUNDLE_KEY));
        }
        startActivity(intent);
    }
}

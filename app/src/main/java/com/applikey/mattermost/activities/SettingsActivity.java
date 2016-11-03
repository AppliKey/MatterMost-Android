package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.SettingsPresenter;
import com.applikey.mattermost.mvp.views.SettingsView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends BaseMvpActivity implements SettingsView {

    @InjectPresenter
    SettingsPresenter mPresenter;

    @Bind(R.id.sw_show_unread_messages)
    SwitchCompat mSwitchShowUnreadMessages;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setTitle(R.string.settings);
    }

    @Override
    public void logout() {
        final Intent intent = new Intent(this, ChooseServerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnCheckedChanged(R.id.sw_show_unread_messages)
    public void onSwitchUnreadTabCheckedChanged(boolean isChecked) {
        mPresenter.setUnreadTabEnabled(isChecked);
    }

    @OnClick(R.id.btn_setting_logout)
    public void onClickLogout() {
        mPresenter.logout();
    }

    @OnClick(R.id.btn_edit_profile)
    public void onClickEditProfile() {
        Toast.makeText(this, "Edit profile not implemented yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setInitialViewState(SettingsPresenter.SettingDataHolder settingDataHolder) {
        mSwitchShowUnreadMessages.setChecked(settingDataHolder.isUnreadTabEnabled());
    }
}

package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.edit.EditProfileActivity;
import com.applikey.mattermost.mvp.presenters.SettingsPresenter;
import com.applikey.mattermost.mvp.views.SettingsView;
import com.applikey.mattermost.platform.socket.WebSocketService;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends BaseMvpActivity implements SettingsView {

    @InjectPresenter
    SettingsPresenter mPresenter;

    @BindView(R.id.sw_show_unread_messages)
    SwitchCompat mSwitchShowUnreadMessages;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setTitle(R.string.settings);
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

    @Override
    public void logout() {
        stopService(WebSocketService.getIntent(this));
        startActivity(ChooseServerActivity.getIntent(this));
    }

    @Override
    public void setInitialViewState(SettingsPresenter.SettingDataHolder settingDataHolder) {
        mSwitchShowUnreadMessages.setChecked(settingDataHolder.isUnreadTabEnabled());
    }

    @OnCheckedChanged(R.id.sw_show_unread_messages)
    void onSwitchUnreadTabCheckedChanged(boolean isChecked) {
        mPresenter.setUnreadTabEnabled(isChecked);
    }

    @OnClick(R.id.btn_setting_logout)
    void onClickLogout() {
        mPresenter.logout();
    }

    @OnClick(R.id.btn_edit_profile)
    void onClickEditProfile() {
        startActivity(EditProfileActivity.getIntent(this));
    }
}

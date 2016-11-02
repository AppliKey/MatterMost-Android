package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.R;
import com.applikey.mattermost.events.UnreadTabStateChangedEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class SettingsActivity extends BaseMvpActivity {

    public static final String KEY_UNREAD_TAB_SETTING = "unread_tabs";
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

    @OnCheckedChanged(R.id.sw_show_unread_messages)
    public void onSwitchUnreadTabCheckedChanged(boolean isChecked) {
        mEventBus.post(new UnreadTabStateChangedEvent(isChecked));
    }
}

package com.applikey.mattermost.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.fragments.SettingsFragment;

public class SettingsActivity extends BaseMvpActivity {

    public static final String KEY_UNREAD_TAB_SETTING = "unread_tabs";

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.settings_fragment_container, SettingsFragment.newInstance())
                .commit();
    }
}

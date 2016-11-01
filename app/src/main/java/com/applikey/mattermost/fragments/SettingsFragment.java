package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.applikey.mattermost.R;

public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
    }
}

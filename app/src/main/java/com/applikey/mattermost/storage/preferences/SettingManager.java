package com.applikey.mattermost.storage.preferences;

import javax.inject.Inject;

public class SettingManager {

    private final Prefs mPrefs;
    private static final String KEY_UNREAD_TABS = "unread_tabs";

    @Inject
    public SettingManager(Prefs prefs) {
        mPrefs = prefs;
    }

    public boolean shouldShowUnreadMessages() {
        return mPrefs.getValue(KEY_UNREAD_TABS, true);
    }
}

package com.applikey.mattermost.storage.preferences;

import javax.inject.Inject;

public class SettingsManager {

    private final Prefs mPrefs;
    private static final String KEY_UNREAD_TABS = "unread_tabs";

    @Inject
    public SettingsManager(Prefs prefs) {
        mPrefs = prefs;
    }

    public boolean shouldShowUnreadMessages() {
        return mPrefs.getValue(KEY_UNREAD_TABS, true);
    }

    public void setUnreadTabState(boolean state) {
        mPrefs.setValue(KEY_UNREAD_TABS, state);
    }

    public void deleteUserSession() {
        mPrefs.setAuthToken(null);
    }

}

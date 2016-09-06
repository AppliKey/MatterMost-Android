package com.applikey.mattermost.web;

import com.applikey.mattermost.storage.preferences.Prefs;

/**
 * Provides current server url using Shard Preferences. Fails with exception if one is not present.
 */
public class ServerUrlFactory {

    private final Prefs mPrefs;

    public ServerUrlFactory(Prefs prefs) {
        mPrefs = prefs;
    }

    public String getServerUrl() {
        final String currentServerUrl = mPrefs.getCurrentServerUrl();
        if (currentServerUrl == null) {
            throw new RuntimeException("No server selected");
        }

        return currentServerUrl;
    }
}

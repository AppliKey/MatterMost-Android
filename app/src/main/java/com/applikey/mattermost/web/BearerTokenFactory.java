package com.applikey.mattermost.web;

import com.applikey.mattermost.storage.preferences.Prefs;

/**
 * Provides bearer token for okhttp reading it from shared preferences.
 */
public class BearerTokenFactory {

    private static final String BEARER_PREFIX = "Bearer ";

    private final Prefs mPrefs;

    public BearerTokenFactory(Prefs prefs) {
        mPrefs = prefs;
    }

    public void setBearerToken(String value) {
        mPrefs.setAuthToken(value);
    }

    public String getBearerTokenString() {
        // TODO Add Caching
        final String value = mPrefs.getAuthToken();
        if (value == null) {
            return null;
        }
        return BEARER_PREFIX + value;
    }
}

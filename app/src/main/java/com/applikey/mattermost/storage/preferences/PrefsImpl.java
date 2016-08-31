package com.applikey.mattermost.storage.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.applikey.mattermost.Constants;

public class PrefsImpl implements Prefs {

    public static final String KEY_USER_ID = Constants.PACKAGE_NAME + ".USER_ID";
    private SharedPreferences mSharedPreferences;

    public PrefsImpl(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public String getCurrentUserId() {
        return mSharedPreferences.getString(KEY_USER_ID, null);
    }

    @Override
    public void setCurrentUserId(@Nullable String id) {
        mSharedPreferences.edit().putString(KEY_USER_ID, id).apply();
    }
}

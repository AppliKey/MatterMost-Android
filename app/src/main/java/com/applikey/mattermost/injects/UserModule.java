package com.applikey.mattermost.injects;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.storage.preferences.Prefs;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
@PerUser
public class UserModule {

    @Provides
    @PerUser
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String provideCurrentUserId(Prefs mPrefs) {
        return mPrefs.getCurrentUserId();
    }
}

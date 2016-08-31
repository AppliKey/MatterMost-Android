package com.applikey.mattermost.injects;


import com.applikey.mattermost.App;

import dagger.Module;
import dagger.Provides;


@Module
public class ApplicationModule {

    private App mApp;

    public ApplicationModule(App app) {
        mApp = app;
    }

    @Provides
    @PerApp
    App provideApplication() {
        return mApp;
    }
}

package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.storage.db.DbImpl;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.images.ImageLoader;
import com.applikey.mattermost.web.images.PicassoImageLoader;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

@Module
public class GlobalModule {

    private App mApp;

    public GlobalModule(App app) {
        mApp = app;
    }

    @Provides
    @PerApp
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @PerApp
    ImageLoader provideImageLoader() {
        return new PicassoImageLoader(mApp);
    }

    @Provides
    @PerApp
    Prefs providePrefs() {
        return new Prefs(mApp);
    }

    @Provides
    @PerApp
    DbImpl provideDb() {
        return new DbImpl(mApp);
    }
}

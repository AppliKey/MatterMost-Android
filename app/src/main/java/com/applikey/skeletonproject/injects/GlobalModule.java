package com.applikey.skeletonproject.injects;

import com.applikey.skeletonproject.App;
import com.applikey.skeletonproject.storage.db.DB;
import com.applikey.skeletonproject.storage.db.DbImpl;
import com.applikey.skeletonproject.storage.preferences.Prefs;
import com.applikey.skeletonproject.storage.preferences.PrefsImpl;
import com.applikey.skeletonproject.web.images.ImageLoader;
import com.applikey.skeletonproject.web.images.PicassoImageLoader;

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
        return new PrefsImpl(mApp);
    }

    @Provides
    @PerApp
    DB provideDb() {
        return new DbImpl(mApp);
    }

}

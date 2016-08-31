package com.applikey.mattermost;


import android.app.Application;

import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.injects.ApplicationModule;
import com.applikey.mattermost.injects.DaggerApplicationComponent;
import com.applikey.mattermost.injects.GlobalModule;
import com.applikey.mattermost.injects.NetworkModule;
import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.web.images.ImageLoader;

import javax.inject.Inject;

public class App extends Application {

    @Inject
    ImageLoader imageLoader;
    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        KissTools.setContext(this);
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule(this))
                .globalModule(new GlobalModule(this))
                .build();
        mComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        imageLoader.dropMemoryCache();
    }
}

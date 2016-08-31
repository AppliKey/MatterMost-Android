package com.applikey.skeletonproject;


import android.app.Application;

import com.applikey.skeletonproject.injects.ApplicationComponent;
import com.applikey.skeletonproject.injects.ApplicationModule;
import com.applikey.skeletonproject.injects.DaggerApplicationComponent;
import com.applikey.skeletonproject.injects.GlobalModule;
import com.applikey.skeletonproject.injects.NetworkModule;
import com.applikey.skeletonproject.utils.kissUtils.KissTools;
import com.applikey.skeletonproject.web.images.ImageLoader;

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

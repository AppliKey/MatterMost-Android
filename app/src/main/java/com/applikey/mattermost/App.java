package com.applikey.mattermost;

import android.app.Application;

import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.injects.DaggerApplicationComponent;
import com.applikey.mattermost.injects.GlobalModule;
import com.applikey.mattermost.injects.UserComponent;
import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.web.images.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {

    @Inject
    ImageLoader imageLoader;

    private static ApplicationComponent mComponent;

    private static UserComponent mUserComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        KissTools.setContext(this);
        mComponent = DaggerApplicationComponent.builder()
                .globalModule(new GlobalModule(this))
                .build();
        mComponent.inject(this);

        Timber.plant(new Timber.DebugTree());

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    public static ApplicationComponent getComponent() {
        return mComponent;
    }

    public static UserComponent getUserComponent() {
        if (mUserComponent == null) {
            mUserComponent = getComponent().userComponentBuilder().build();
        }
        return mUserComponent;
    }

    public static void releaseUserComponent() {
        mUserComponent = null;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        imageLoader.dropMemoryCache();
    }
}

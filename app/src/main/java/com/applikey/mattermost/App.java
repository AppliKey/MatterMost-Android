package com.applikey.mattermost;

import android.app.Application;

import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.injects.DaggerApplicationComponent;
import com.applikey.mattermost.injects.GlobalModule;
import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.web.images.ImageLoader;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

public class App extends Application {

    @Inject
    ImageLoader imageLoader;

    private static ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        KissTools.setContext(this);
        mComponent = DaggerApplicationComponent.builder()
                .globalModule(new GlobalModule(this))
                .build();
        mComponent.inject(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    public static ApplicationComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        imageLoader.dropMemoryCache();
    }
}

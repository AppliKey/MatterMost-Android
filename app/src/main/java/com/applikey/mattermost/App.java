package com.applikey.mattermost;

import android.app.Application;
import android.util.Log;

import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.activities.ChatListActivity;
import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.injects.DaggerApplicationComponent;
import com.applikey.mattermost.injects.GlobalModule;
import com.applikey.mattermost.injects.UserComponent;
import com.applikey.mattermost.manager.RxForeground;
import com.applikey.mattermost.platform.socket.WebSocketService;
import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.web.images.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

public class App extends Application {

    private static final String TAG = "App";

    private static ApplicationComponent mComponent;
    private static UserComponent mUserComponent;

    @Inject
    ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(false)
                .build();

        Fabric.with(fabric);

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

        RxForeground.with(this)
                .observeForeground(ChatListActivity.class, ChatActivity.class)
                .doOnNext(observe -> Log.d(TAG, "Chat activities are on " + (observe ? "foreground" : "background")))
                .switchMap(foreground -> Observable.timer(foreground ? 0 : Constants.SOCKET_SERVICE_SHUTDOWN_THRESHOLD_MINUTES, TimeUnit.MINUTES)
                        .map(tick -> foreground))
                .subscribe(foreground -> {
                    if (foreground) {
                        startService(WebSocketService.getIntent(this));
                    } else {
                        stopService(WebSocketService.getIntent(this));
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        imageLoader.dropMemoryCache();
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

}

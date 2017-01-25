package com.applikey.mattermost;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.content.Intent;
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
import com.fuck_boilerplate.rx_paparazzo.RxPaparazzo;
import com.squareup.leakcanary.LeakCanary;
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
        MultiDex.install(this);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(false)
                .build();

        Fabric.with(fabric);
        RxPaparazzo.register(this);
        LeakCanary.install(this);

        KissTools.setContext(this);
        mComponent = DaggerApplicationComponent.builder()
                .globalModule(new GlobalModule(this))
                .build();
        mComponent.inject(this);

        Timber.plant(new Timber.DebugTree());

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                                .withLimit(2000)
                                .build())
                        .build());

        manageMessagingService();
    }

    private void manageMessagingService() {
        final Intent serviceIntent = WebSocketService.getIntent(this);
        RxForeground.with(this)
                .observeForeground(ChatListActivity.class, ChatActivity.class)
                .doOnNext(observe -> Log.d(TAG, "Chat activities are on " + (observe ? "foreground" : "background")))
                .switchMap(foreground -> Observable.timer(
                        foreground ? 0 : Constants.SOCKET_SERVICE_SHUTDOWN_THRESHOLD_MINUTES, TimeUnit.MINUTES)
                        .map(tick -> foreground))
                .subscribe(foreground -> {
                    if (foreground) {
                        startService(serviceIntent);
                    } else {
                        stopService(serviceIntent);
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

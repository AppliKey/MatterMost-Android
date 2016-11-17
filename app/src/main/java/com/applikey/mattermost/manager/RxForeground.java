package com.applikey.mattermost.manager;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Emitter;
import rx.Observable;

public class RxForeground {

    private static final String TAG = "ForegroundManager";

    private final Application mApp;
    private final Set<Class<? extends Activity>> mClasses;
    private volatile boolean mIsForeground;

    public static RxForeground with(Context context) {
        return new RxForeground(context);
    }

    private RxForeground(Context context) {
        mApp = ((Application) context.getApplicationContext());
        mClasses = new HashSet<>();
    }

    @SafeVarargs
    public final RxForeground ofActivities(@NonNull Class<? extends Activity>... classes) {
        mClasses.clear();
        mClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public final Observable<Boolean> observe() {
        final Observable<Boolean> observable = Observable.fromEmitter(emitter -> {
            final LifecycleCallback lifecycleCallback = new LifecycleCallback() {
                @Override
                public void onActivityResumed(Activity activity) {
                    if (mClasses.isEmpty()) {
                        emitter.onNext(true);
                        mIsForeground = true;
                        return;
                    }
                    for (Class clazz : mClasses) {
                        if (clazz.isInstance(activity)) {
                            emitter.onNext(true);
                            mIsForeground = true;
                            return;
                        }
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (mClasses.isEmpty()) {
                        emitter.onNext(false);
                        mIsForeground = false;
                        return;
                    }
                    for (Class clazz : mClasses) {
                        if (clazz.isInstance(activity)) {
                            emitter.onNext(false);
                            mIsForeground = false;
                            return;
                        }
                    }
                }
            };

            mApp.registerActivityLifecycleCallbacks(lifecycleCallback);
            emitter.setCancellation(() -> mApp.unregisterActivityLifecycleCallbacks(lifecycleCallback));

        }, Emitter.BackpressureMode.LATEST);
        return observable.debounce(500, TimeUnit.MILLISECONDS).distinctUntilChanged();
    }

    public boolean isForeground() {
        return mIsForeground;
    }

    private abstract static class LifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

}

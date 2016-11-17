package com.applikey.mattermost.manager;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import rx.Emitter;
import rx.Observable;

public class ForegroundManager {

    private static final String TAG = "ForegroundManager";
    private final Application mApp;

    public ForegroundManager(Context context) {
        mApp = ((Application) context.getApplicationContext());
    }

    @SafeVarargs
    public final Observable<Boolean> foreground(Class<? extends Activity>... classes) {
        final Observable<Boolean> observable = Observable.fromEmitter(emitter -> {
            final LifecycleCallback lifecycleCallback = new LifecycleCallback() {
                @Override
                public void onActivityResumed(Activity activity) {
                    if (classes.length == 0) {
                        emitter.onNext(true);
                        return;
                    }
                    for (Class clazz : classes) {
                        if (clazz.equals(activity.getClass())) {
                            emitter.onNext(true);
                            return;
                        }
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (classes.length == 0) {
                        emitter.onNext(false);
                        return;
                    }
                    for (Class clazz : classes) {
                        if (clazz.equals(activity.getClass())) {
                            emitter.onNext(false);
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

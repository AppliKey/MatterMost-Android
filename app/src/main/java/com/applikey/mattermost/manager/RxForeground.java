package com.applikey.mattermost.manager;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import rx.Emitter;
import rx.Observable;

public class RxForeground {

    private static final String TAG = "RxForeground";

    private final Application mApp;

    public static RxForeground with(Context context) {
        return new RxForeground(context);
    }

    private RxForeground(Context context) {
        mApp = ((Application) context.getApplicationContext());
    }

    /**
     * Observe activity foreground status. Activity is on <b>foreground</b> when its <code>onResume</code> lifecycle callback fired
     * and on <b>background</b> when its <code>onPause</code> fired.
     *
     * @param classes Activities' classes to observe foreground on.
     * If <b>no</b> args provided observe <b>entire</b> application foreground
     *
     * @return RxJava Observable with foreground status.
     * True if any of activities from args is on foreground false otherwise
     */
    @SafeVarargs
    public final Observable<Boolean> observeForeground(@NonNull Class<? extends Activity>... classes) {
        final Observable<Boolean> observable = Observable.fromEmitter(emitter -> {
            final LifecycleCallback lifecycleCallback = new LifecycleCallback() {
                @Override
                public void onActivityResumed(Activity activity) {
                    if (classes.length == 0) {
                        emitter.onNext(true);
                        return;
                    }
                    for (Class clazz : classes) {
                        if (clazz.isInstance(activity)) {
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
                        if (clazz.isInstance(activity)) {
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

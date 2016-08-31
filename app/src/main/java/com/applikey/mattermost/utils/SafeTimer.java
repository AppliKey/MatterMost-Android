package com.applikey.mattermost.utils;


import android.os.Handler;
import android.os.Looper;

public class SafeTimer extends Handler {

    private Runnable mRunnable;
    private int mDelaySec;
    private Runnable mMyRunnable;

    public SafeTimer(Runnable runnable, int delayInSec) {
        super(Looper.getMainLooper());
        mRunnable = runnable;
        mDelaySec = delayInSec;
    }

    /**
     * Should be called in activity/fragment onResume method
     */
    public void start() {
        if (mMyRunnable == null) {
            mMyRunnable = new Runnable() {
                @Override
                public void run() {
                    mRunnable.run();
                    postDelayed(this, mDelaySec * 1000);
                }
            };
        }
        post(mMyRunnable);
    }

    /**
     * Should be called in activity/fragment onPause method to avoid leaks
     */
    public void finish() {
        if (mMyRunnable != null) {
            removeCallbacks(mMyRunnable);
        }
    }
}

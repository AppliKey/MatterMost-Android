package com.applikey.skeletonproject.utils.rx;


import android.util.Log;

import rx.Observer;

public class LoggerObserver implements Observer {


    private static final String TAG = "LoggerObserver";

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: ", e);
    }

    @Override
    public void onNext(Object o) {

    }
}

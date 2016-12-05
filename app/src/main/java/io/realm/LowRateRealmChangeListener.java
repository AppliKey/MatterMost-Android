package io.realm;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public abstract class LowRateRealmChangeListener<T> implements RealmChangeListener<T> {

    private boolean mShouldCallBack;
    private final Subscription mSubscription;

    public LowRateRealmChangeListener() {
        mSubscription = Observable
                .interval(5, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((v) -> {
                    if (mShouldCallBack) {
                        Log.d(LowRateRealmChangeListener.class.getSimpleName(), "Data set change");
                        onChangeCallback();
                        mShouldCallBack = false;
                    }
                }, e -> Log.e(LowRateRealmChangeListener.class.getSimpleName(), e.getMessage()));
    }

    @Override
    public void onChange(T element) {
        mShouldCallBack = true;
    }

    public void unSubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public abstract void onChangeCallback();
}

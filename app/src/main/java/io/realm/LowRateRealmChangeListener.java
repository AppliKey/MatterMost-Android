package io.realm;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public abstract class LowRateRealmChangeListener<T> implements RealmChangeListener<T> {

    private boolean mShouldCallBack;
    private final Subscription mSubscription;

    LowRateRealmChangeListener() {
        mSubscription = Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    if (mShouldCallBack) {
                        onChangeCallback();
                        mShouldCallBack = false;
                    }
                }, e -> Log.e(LowRateRealmChangeListener.class.getSimpleName(), e.getMessage()));
    }

    @Override
    public void onChange(T element) {
        mShouldCallBack = true;
    }

    void unSubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public abstract void onChangeCallback();
}

package com.applikey.mattermost.utils.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RxUtils {

    public static <T> Observable.Transformer<T, T> applySchedulersAndRetry() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay.getDefaultInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

package com.applikey.mattermost.utils.rx;


import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class RetryWithDelay
        implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        retryCount = 0;
    }

    public static RetryWithDelay getDefaultInstance() {
        return new RetryWithDelay(5, 250);
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts.flatMap(throwable -> {
            if (++retryCount < maxRetries) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).

                return Observable.timer(retryCount * retryDelayMillis,
                        TimeUnit.MILLISECONDS);
            }

            // Max retries hit. Just pass the error along.
            return Observable.error(throwable);
        });
    }
}

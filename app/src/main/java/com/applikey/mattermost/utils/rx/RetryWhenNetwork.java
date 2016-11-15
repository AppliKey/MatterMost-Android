package com.applikey.mattermost.utils.rx;


import android.content.Context;

import com.applikey.mattermost.utils.ConnectivityUtils;

import rx.Observable;
import rx.functions.Func1;

public class RetryWhenNetwork implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final Context mContext;

    public RetryWhenNetwork(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> errors) {
        return errors.switchMap(throwable -> ConnectivityUtils.getConnectivityObservable(mContext).takeFirst(status -> status));
    }
}

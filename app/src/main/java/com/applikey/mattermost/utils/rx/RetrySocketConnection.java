package com.applikey.mattermost.utils.rx;


import android.content.Context;

import com.applikey.mattermost.utils.ConnectivityUtils;
import com.neovisionaries.ws.client.WebSocketError;
import com.neovisionaries.ws.client.WebSocketException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class RetrySocketConnection implements Func1<Observable<? extends Throwable>, Observable<?>> {

    public RetrySocketConnection(Context context) {
        mContext = context.getApplicationContext();
    }

    private Context mContext;

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.flatMap(throwable -> {
            if (throwable instanceof WebSocketException && ((WebSocketException) throwable).getError() == WebSocketError.SOCKET_CONNECT_ERROR) {
                return ConnectivityUtils.getConnectivityObservable(mContext).takeFirst(status -> status);
            } else {
                return Observable.zip(Observable.just(throwable), Observable.range(1, 5), (o, integer) -> integer)
                        .flatMap(i -> i == 3
                                ? Observable.error(throwable)
                                : Observable.timer(1 << i, TimeUnit.SECONDS));
            }
        });
    }
}

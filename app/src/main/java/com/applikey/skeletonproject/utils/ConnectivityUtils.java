package com.applikey.skeletonproject.utils;


import android.content.Context;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConnectivityUtils {

    @NonNull
    Observable<Boolean> getConnectivityObservable(@NonNull Context context) {
        return new ReactiveNetwork()
                .enableInternetCheck()
                .observeConnectivity(context)
                .subscribeOn(Schedulers.io())
                .map(connectivityStatus ->
                        ConnectivityStatus.MOBILE_CONNECTED.equals(connectivityStatus) ||
                                ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET.equals(
                                        connectivityStatus))
                .observeOn(AndroidSchedulers.mainThread());
    }
}

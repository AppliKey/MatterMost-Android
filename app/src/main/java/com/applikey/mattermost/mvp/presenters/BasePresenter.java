package com.applikey.mattermost.mvp.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import rx.subscriptions.CompositeSubscription;

/**
 * Simplifies getting view. Assumes that we don't care if there are multiple views
 * attached and get the random one.
 */
public abstract class BasePresenter<T extends MvpView> extends MvpPresenter<T> {

    protected CompositeSubscription mSubscription = new CompositeSubscription();

    private void unSubscribe() {
        mSubscription.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }
}

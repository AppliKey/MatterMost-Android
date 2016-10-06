package com.applikey.mattermost.mvp.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import java.util.Set;

import rx.subscriptions.CompositeSubscription;

/**
 * Simplifies getting view. Assumes that we don't care if there are multiple views
 * attached and get the random one.
 */
/* package */ abstract class BasePresenter<T extends MvpView> extends MvpPresenter<T>
        implements UnsubscribeablePresenter {

    /* package */ CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    public void unSubscribe() {
        mSubscription.clear();
    }
}

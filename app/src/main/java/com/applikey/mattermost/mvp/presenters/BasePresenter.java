package com.applikey.mattermost.mvp.presenters;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.applikey.mattermost.utils.rx.lifecycle.PresenterEvent;
import com.applikey.mattermost.utils.rx.lifecycle.RxLifecyclePresenter;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Simplifies getting view. Assumes that we don't care if there are multiple views
 * attached and get the random one.
 */
public abstract class BasePresenter<T extends MvpView> extends MvpPresenter<T>
        implements LifecycleProvider<PresenterEvent> {

    protected CompositeSubscription mSubscription = new CompositeSubscription();
    private final BehaviorSubject<PresenterEvent> lifecycleSubject = BehaviorSubject.create();

    @CallSuper
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        lifecycleSubject.onNext(PresenterEvent.CREATE);
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<PresenterEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <K> LifecycleTransformer<K> bindUntilEvent(@NonNull PresenterEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <K> LifecycleTransformer<K> bindToLifecycle() {
        return RxLifecyclePresenter.bindPresenter(lifecycleSubject);
    }

    @CallSuper
    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(PresenterEvent.DESTROY);
        mSubscription.clear();
        super.onDestroy();
    }
}

package com.applikey.mattermost.utils.rx.lifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.OutsideLifecycleException;

import rx.Observable;
import rx.functions.Func1;

import static com.trello.rxlifecycle.RxLifecycle.bind;

public class RxLifecyclePresenter {

    private RxLifecyclePresenter() {
        throw new AssertionError("No instances");
    }

    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindPresenter(@NonNull final Observable<PresenterEvent> lifecycle) {
        return bind(lifecycle, PRESENTER_LIFECYCLE);
    }

    private static final Func1<PresenterEvent, PresenterEvent> PRESENTER_LIFECYCLE =
            lastEvent -> {
                switch (lastEvent) {
                    case CREATE:
                        return PresenterEvent.DESTROY;
                    case DESTROY:
                        throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                    default:
                        throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                }
            };
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.RestorePasswordView;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class RestorePasswordPresenter extends SingleViewPresenter<RestorePasswordView> {

    @Inject
    Api mApi;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    public RestorePasswordPresenter() {
        App.getComponent().inject(this);
    }

    public void sendRestorePasswordRequest(String email) {
        final RestorePasswordView view = getView();
        mSubscription.add(mApi.sendPasswordReset(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    view.onPasswordRestoreSent();
                }, throwable -> {
                    ErrorHandler.handleError(throwable);
                    view.onFailure(throwable.getMessage());
                }));
    }

    public void unSubscribe() {
        mSubscription.unsubscribe();
    }
}

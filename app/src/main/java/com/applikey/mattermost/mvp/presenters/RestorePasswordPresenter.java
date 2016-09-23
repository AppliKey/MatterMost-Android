package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.RestorePasswordView;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class RestorePasswordPresenter extends SingleViewPresenter<RestorePasswordView> {

    private static final String INVALID_EMAIL_MESSAGE = "Invalid Email";

    @Inject
    Api mApi;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    public RestorePasswordPresenter() {
        App.getComponent().inject(this);
    }

    public void sendRestorePasswordRequest(String email) {
        final RestorePasswordView view = getView();

        if (!validateEmailFormat(email)) {
            view.onFailure(INVALID_EMAIL_MESSAGE);
            return;
        }

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
        mSubscription.clear();
    }

    private boolean validateEmailFormat(String email) {
        return !email.trim().isEmpty() && StringUtil.isEmail(email);
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.auth.RestorePasswordRequest;
import com.applikey.mattermost.mvp.views.RestorePasswordView;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.utils.rx.RxUtils;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class RestorePasswordPresenter extends BasePresenter<RestorePasswordView> {

    @Inject
    Api mApi;

    @Inject
    Lazy<ErrorHandler> mErrorHandler;

    public RestorePasswordPresenter() {
        App.getComponent().inject(this);
    }

    public void sendRestorePasswordRequest(String email) {
        final RestorePasswordView view = getViewState();
        if (!validateEmailFormat(email)) {
            view.onFailure(null);
            return;
        }

        final Subscription subscribe = mApi.sendPasswordReset(new RestorePasswordRequest(email))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.applyProgress(view::showLoading, view::hideLoading))
                .subscribe(v -> view.onPasswordRestoreSent(), throwable -> {
                    view.onFailure(mErrorHandler.get().getErrorMessage(throwable));
                });

        mSubscription.add(subscribe);
    }

    private boolean validateEmailFormat(String email) {
        return !email.trim().isEmpty() && StringUtil.isEmail(email);
    }
}

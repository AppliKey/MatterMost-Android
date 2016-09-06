package com.applikey.mattermost.mvp.presenters;

import android.app.Activity;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.mvp.views.LogInView;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LogInPresenter extends SingleViewPresenter<LogInView> {

    @Inject
    Api mApi;

    public LogInPresenter() {
        App.getComponent().inject(this);
    }

    // TODO: pre-validation. NOTE: Use stringutils
    public void authorize(final Activity context, String email, String password) {
        final LogInView view = getView();
        final AuthenticationRequest request = new AuthenticationRequest(email, password);

        mApi.authorize(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signInResponse -> {
//                    mPrefs.saveAccessCredentials(signInResponse);
                    view.onSuccessfulAuth();
                }, throwable -> {
                    ErrorHandler.handleError(context, throwable);
                    view.onUnsuccessfulAuth(throwable);
                });
    }
}

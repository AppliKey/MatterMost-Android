package com.applikey.mattermost.mvp.presenters;

import android.app.Activity;
import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.mvp.views.LogInView;
import com.applikey.mattermost.storage.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

// TODO unsubscribe composite subscription OR migrate to RxPresenter (like RxFragment and RxActivity)
public class LogInPresenter extends SingleViewPresenter<LogInView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage teamStorage;

    private CompositeSubscription mSubscription;

    public LogInPresenter() {
        App.getComponent().inject(this);

        mSubscription = new CompositeSubscription();
    }

    // TODO: pre-validation. NOTE: Use stringutils
    public void authorize(final Activity context, String email, String password) {
        final LogInView view = getView();
        // TODO Set team
        final AuthenticationRequest request = new AuthenticationRequest("", email, password);

        mSubscription.add(mApi.authorize(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signInResponse -> {
                    cacheHeaders(signInResponse);
                    view.onSuccessfulAuth();
                }, throwable -> {
                    ErrorHandler.handleError(context, throwable);
                    view.onUnsuccessfulAuth(throwable);
                }));
    }

    public void getInitialData() {
        Log.d("LogInPresenter", "getInitialData");

        mSubscription.add(teamStorage.listAll()
                .subscribe(entries -> {
                    getView().displayTeams(entries);
                }));
    }

    private void cacheHeaders(Response<AuthenticationResponse> response) {
        final Headers headers = response.headers();
        final String authenticationToken = headers.get("token");

        mPrefs.setKeyAuthToken(authenticationToken);
    }
}

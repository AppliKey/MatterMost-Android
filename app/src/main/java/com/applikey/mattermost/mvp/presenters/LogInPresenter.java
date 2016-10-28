package com.applikey.mattermost.mvp.presenters;

import android.app.Activity;

import com.applikey.mattermost.App;
import com.applikey.mattermost.BuildConfig;
import com.applikey.mattermost.models.auth.AttachDeviceRequest;
import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.web.RequestError;
import com.applikey.mattermost.mvp.views.LogInView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class LogInPresenter extends BasePresenter<LogInView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    BearerTokenFactory mTokenFactory;

    @Inject
    TeamStorage mTeamStorage;

    public LogInPresenter() {
        App.getComponent().inject(this);
    }

    // TODO: pre-validation. NOTE: Use stringutils
    public void authorize(Activity context, String email, String password) {
        final LogInView view = getViewState();
        final AuthenticationRequest request = new AuthenticationRequest(email, password);

        mSubscription.add(mApi.authorize(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSuccessfulResponse, throwable -> {
                    ErrorHandler.handleError(context, throwable);
                    view.onUnsuccessfulAuth(throwable.getMessage());
                }));
    }

    public void getInitialData() {
        final String userName = BuildConfig.USER_NAME;
        final String userPassword = BuildConfig.USER_PASSWORD;
        final boolean shouldReplaceCredentials = BuildConfig.SHOULD_REPLACE_CREDENTIALS;

        // We assume that if username is set, we also set password
        //noinspection ConstantConditions,PointlessBooleanExpression
        if (shouldReplaceCredentials && userName != null && !userName.isEmpty()) {
            getViewState().showPresetCredentials(userName, userPassword);
        }
    }

    public void loadTeams() {
        final LogInView view = getViewState();
        mSubscription.add(mApi.listTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    mTeamStorage.saveTeamsWithRemoval(response.values());
                    view.onTeamsRetrieved(response);
                }, view::onTeamsReceiveFailed));
    }

    private void handleSuccessfulResponse(Response<AuthenticationResponse> response) {
        final int code = response.code();

        final int codesGroup = code / 100;

        // Handle success
        if (codesGroup == 2) {
            cacheHeaders(response);

            mPrefs.setCurrentUserId(response.body().getId());

            // TODO Refactor
            final AttachDeviceRequest request = new AttachDeviceRequest();
            request.setDeviceId("android:" + mPrefs.getGcmToken());

            mSubscription.add(mApi.attachDevice(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> getViewState().onSuccessfulAuth(), ErrorHandler::handleError));
            return;
        }

        // Handle failure
        try {
            final String message = RequestError.fromJson(response.errorBody().string())
                    .getMessage();
            getViewState().onUnsuccessfulAuth(message);
            ErrorHandler.handleError(message);
        } catch (IOException e) {
            ErrorHandler.handleError(e);
        }
    }

    private void cacheHeaders(Response<AuthenticationResponse> response) {
        final Headers headers = response.headers();
        final String authenticationToken = headers.get("Token");

        mTokenFactory.setBearerToken(authenticationToken);
    }
}

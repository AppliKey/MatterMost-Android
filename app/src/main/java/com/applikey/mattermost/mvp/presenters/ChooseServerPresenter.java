package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChooseServerPresenter extends BasePresenter<ChooseServerView> {

    private static final String TAG = ChooseServerPresenter.class.getSimpleName();

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String URL_END_DELIMITER = "/";

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage teamStorage;

    public ChooseServerPresenter() {
        App.getComponent().inject(this);
    }

    public void chooseServer(String httpPrefix, String serverUrl) {
        final ChooseServerView view = getViewState();

        String fullServerUrl = serverUrl;

        if (!serverUrl.startsWith(HTTP_PREFIX) && !serverUrl.startsWith(HTTPS_PREFIX)) {
            fullServerUrl = httpPrefix + serverUrl;
        }

        if (!fullServerUrl.endsWith(URL_END_DELIMITER)) {
            fullServerUrl += URL_END_DELIMITER;
        }

        if (!validateServer(fullServerUrl)) {
            view.showValidationError();
            return;
        }

        mPrefs.setCurrentServerUrl(fullServerUrl);

        mSubscription.add(mApi.ping()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pingResponse -> {
                    view.onValidServerChosen();
                }, throwable -> {
                    ErrorHandler.handleError(throwable);
                    view.showValidationError();
                }));
    }

    // We validate the same way Retrofit does
    private boolean validateServer(String serverUrl) {
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            return false;
        }
        HttpUrl url;
        try {
            url = HttpUrl.parse(serverUrl);
        } catch (Exception ignored) {
            return false;
        }

        return url != null;
    }
}

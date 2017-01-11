package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.BuildConfig;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.PersistentPrefs;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;
import okhttp3.HttpUrl;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChooseServerPresenter extends BasePresenter<ChooseServerView> {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String URL_END_DELIMITER = "/";

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    PersistentPrefs mPersistentPrefs;

    @Inject
    TeamStorage teamStorage;

    @Inject
    Lazy<ErrorHandler> mErrorHandler;

    public ChooseServerPresenter() {
        App.getComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getInitialData();
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

        final String url = fullServerUrl;

        mSubscription.add(mApi.ping()
                                  .subscribeOn(Schedulers.io())
                                  .doOnSuccess(pingResponse -> {
                                      final String version = pingResponse.getVersion();
                                      final String[] versionDigits = version.split("\\.");
                                      final int major = Integer.parseInt(versionDigits[0]);
                                      final int minor = Integer.parseInt(versionDigits[1]);
                                      mPrefs.setServerVersion(version);
                                      mPrefs.setServerVersionMajor(major);
                                      mPrefs.setServerVersionMinor(minor);
                                  })
                                  .flatMap(response -> mPersistentPrefs.saveServerUrl(url))
                                  .flatMap(s -> mPersistentPrefs.getServerUrls())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(stringSet -> {
                                      final String[] urls = new String[stringSet.size()];
                                      stringSet.toArray(urls);
                                      view.setAutoCompleteServers(urls);
                                      view.onValidServerChosen();
                                  }, throwable -> {
                                      mErrorHandler.get().handleError(throwable);
                                      view.showValidationError();
                                  }));
    }

    private void getInitialData() {
        mSubscription.add(mPersistentPrefs.getServerUrls()
                                  .map(urls -> urls.toArray(new String[urls.size()]))
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(getViewState()::setAutoCompleteServers,
                                             throwable -> mErrorHandler.get().handleError(throwable)));

        final String presetServer = BuildConfig.PRESET_SERVER;
        final boolean shouldReplaceCredentials = BuildConfig.SHOULD_REPLACE_CREDENTIALS;

        // We assume that if username is set, we also set password
        //noinspection ConstantConditions,PointlessBooleanExpression
        if (shouldReplaceCredentials && presetServer != null && !presetServer.isEmpty()) {
            getViewState().showPresetServer(presetServer);
        }

    }

    // We validate the same way Retrofit does
    private boolean validateServer(String serverUrl) {
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            return false;
        }
        final HttpUrl url;
        try {
            url = HttpUrl.parse(serverUrl);
        } catch (Exception ignored) {
            return false;
        }

        return url != null;
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChooseServerPresenter extends SingleViewPresenter<ChooseServerView> {

    private static final String URL_END_DELIMITER = "/";

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    public ChooseServerPresenter() {
        App.getComponent().inject(this);
    }

    public void chooseServer(String httpPrefix, String serverUrl) {
        final ChooseServerView view = getView();
        if (!validateServer(serverUrl)) {
            view.showValidationError();
            return;
        }
        String fullServerUrl = httpPrefix + serverUrl;
        if (!fullServerUrl.endsWith(URL_END_DELIMITER)) {
            fullServerUrl += URL_END_DELIMITER;
        }
        mPrefs.setCurrentServerUrl(fullServerUrl);

        // TODO Save teams
        mApi.listTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onTeamsRetrieved, view::onTeamsReceiveFailed);
    }

    // TODO Add proper validation
    private boolean validateServer(String serverUrl) {
        return !(serverUrl == null || serverUrl.trim().isEmpty());
    }
}

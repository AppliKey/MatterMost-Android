package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;

import javax.inject.Inject;

public class ChooseServerPresenter extends SingleViewPresenter<ChooseServerView> {

    private static final String TAG = ChooseServerPresenter.class.getSimpleName();

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

        Log.d(TAG, "chooseServer: Start");

        final ChooseServerView view = getView();

        Log.d(TAG, "chooseServer: getView");

        if (!validateServer(serverUrl)) {
            view.showValidationError();
            return;
        }
        String fullServerUrl = httpPrefix + serverUrl;
        if (!fullServerUrl.endsWith(URL_END_DELIMITER)) {
            fullServerUrl += URL_END_DELIMITER;
        }
        mPrefs.setCurrentServerUrl(fullServerUrl);

        view.onValidServerChosen();
    }

    // TODO Add proper validation
    private boolean validateServer(String serverUrl) {
        return !(serverUrl == null || serverUrl.trim().isEmpty());
    }
}

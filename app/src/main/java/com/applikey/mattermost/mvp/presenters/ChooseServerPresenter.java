package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Set;

import javax.inject.Inject;

public class ChooseServerPresenter extends SingleViewPresenter<ChooseServerView> {

    private static final String URL_END_DELIMITER = "/";

    @Inject
    Prefs mPrefs;

    public ChooseServerPresenter() {
        App.getComponent().inject(this);
    }

    public void chooseServer(String httpPrefix, String serverUrl) {
        if (!validateServer(serverUrl)) {
            getView().showValidationError();
            return;
        }
        String fullServerUrl = httpPrefix + serverUrl;
        if (!fullServerUrl.endsWith(URL_END_DELIMITER)) {
            fullServerUrl += URL_END_DELIMITER;
        }
        mPrefs.setCurrentServerUrl(fullServerUrl);
        getView().onValidServerEntered();
    }

    // TODO Add proper validation
    private boolean validateServer(String serverUrl) {
        return !(serverUrl == null || serverUrl.trim().isEmpty());
    }
}

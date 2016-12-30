package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.SplashView;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {

    @Inject
    Prefs mPrefs;

    public SplashPresenter() {
        App.getComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        checkSession();
    }

    private void checkSession() {
        final boolean isSessionExist = mPrefs.getAuthToken() != null;
        getViewState().isSessionExist(isSessionExist);
    }
}

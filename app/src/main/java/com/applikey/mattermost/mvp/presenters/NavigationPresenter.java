package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;

@InjectViewState
public class NavigationPresenter extends BasePresenter<NavigationView> {

    @Inject
    Lazy<StorageDestroyer> mStorageDestroyer;

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    public NavigationPresenter() {
        App.getComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public void logout() {
        mPrefs.setAuthToken(null);
        App.releaseUserComponent();
        mStorageDestroyer.get().deleteDatabase();
        getViewState().onLogout();
    }
}

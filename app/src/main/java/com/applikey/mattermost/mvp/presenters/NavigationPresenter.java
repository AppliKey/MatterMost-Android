package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;

@InjectViewState
public class NavigationPresenter extends BasePresenter<NavigationView> {

    @Inject
    Lazy<StorageDestroyer> mStorageDestroyer;

    @Inject
    Lazy<Prefs> mPrefs;

    public NavigationPresenter() {
        App.getUserComponent().inject(this);
    }

    public void createNewChannel() {
        getViewState().startChannelCreating();
    }
}

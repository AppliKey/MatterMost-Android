package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.SettingsView;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.storage.preferences.SettingsManager;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

@InjectViewState
public class SettingsPresenter extends BasePresenter<SettingsView> {

    @Inject
    Lazy<StorageDestroyer> mStorageDestroyer;

    @Inject
    SettingsManager mSettingsManager;

    @Inject
    Prefs mPrefs;

    public SettingsPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setInitialViewState(getInitialSettingData());

    }

    private SettingDataHolder getInitialSettingData() {
        final boolean isUnreadTabEnabled = mSettingsManager.shouldShowUnreadMessages();
        return new SettingDataHolder(isUnreadTabEnabled);
    }

    public void logout() {
        mSettingsManager.deleteUserSession();
        App.releaseUserComponent();
        mStorageDestroyer.get().deleteDatabase();
        getViewState().logout();
    }

    public void setUnreadTabEnabled(boolean enabled) {
        mSettingsManager.setUnreadTabState(enabled);
        Timber.d("unread tab new state: %b", mSettingsManager.shouldShowUnreadMessages());
    }


    public static class SettingDataHolder {
        private boolean isUnreadTabEnabled;

        public SettingDataHolder(boolean isUnreadTabEnabled) {
            this.isUnreadTabEnabled = isUnreadTabEnabled;
        }

        public boolean isUnreadTabEnabled() {
            return isUnreadTabEnabled;
        }
    }

}

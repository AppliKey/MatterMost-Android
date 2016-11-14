package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.SettingsView;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.storage.preferences.SettingsManager;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import dagger.Lazy;

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

    public void logout() {
        getViewState().logout();
        mSettingsManager.deleteUserSession();
        mStorageDestroyer.get().deleteDatabase();
        mPrefs.clear();
        App.releaseUserComponent();
    }

    public void setUnreadTabEnabled(boolean enabled) {
        mSettingsManager.setUnreadTabState(enabled);
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

    public static class SettingDataHolder {

        private final boolean isUnreadTabEnabled;

        public SettingDataHolder(boolean isUnreadTabEnabled) {
            this.isUnreadTabEnabled = isUnreadTabEnabled;
        }

        public boolean isUnreadTabEnabled() {
            return isUnreadTabEnabled;
        }
    }

}

package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.mvp.presenters.SettingsPresenter;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndStrategy.class)
public interface SettingsView extends MvpView {
    void logout();

    void setInitialViewState(SettingsPresenter.SettingDataHolder settingDataHolder);
}

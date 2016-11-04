package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.mvp.presenters.SettingsPresenter;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface SettingsView extends MvpView {
    @StateStrategyType(SkipStrategy.class)
    void logout();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setInitialViewState(SettingsPresenter.SettingDataHolder settingDataHolder);
}

package com.applikey.mattermost.mvp.views.edit;

import android.support.annotation.Nullable;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.io.File;

public interface EditProfileView extends MvpView {

    @StateStrategyType(SingleStateStrategy.class)
    void showLoading();

    @StateStrategyType(SingleStateStrategy.class)
    void hideLoading();

    @StateStrategyType(SingleStateStrategy.class)
    void onUserAttached(User user);

    @StateStrategyType(SingleStateStrategy.class)
    void onImageChosen(File file);

    @StateStrategyType(SkipStrategy.class)
    void showUsernameValidationError(@Nullable String cause);

    @StateStrategyType(SkipStrategy.class)
    void showEmailValidationError(@Nullable String cause);

    @StateStrategyType(SkipStrategy.class)
    void showError(String message);
}

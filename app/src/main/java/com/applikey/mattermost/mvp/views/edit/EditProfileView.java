package com.applikey.mattermost.mvp.views.edit;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface EditProfileView extends MvpView {

    @StateStrategyType(SingleStateStrategy.class)
    void onUserAttached(User user);

    @StateStrategyType(SingleStateStrategy.class)
    void onImageChosen(String uri);
}

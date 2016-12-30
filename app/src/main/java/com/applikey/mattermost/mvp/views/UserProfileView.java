package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface UserProfileView extends MvpView, FavoriteableView {

    void showBaseDetails(User user);

    void onMakeFavorite(boolean favorite);

    @StateStrategyType(value = OneExecutionStateStrategy.class)
    void openDirectChannel(Channel channel);
}

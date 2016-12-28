package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface ChannelDetailsView extends MvpView, FavoriteableView {

    void showBaseDetails(Channel channel);

    void showMembers(List<User> users);

    @StateStrategyType(SkipStrategy.class)
    void openEditChannel(Channel channel, boolean invite);

    void showProgress(boolean show);

    void backToMainActivity();
}

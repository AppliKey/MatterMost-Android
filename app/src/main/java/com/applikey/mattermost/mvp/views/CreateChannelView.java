package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface CreateChannelView extends BaseEditChannelView {
    void onChannelCreated();
    void setButtonAddAllState(boolean isAllAlreadyInvited);
}

package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * @author Anatoliy Chub
 */
@StateStrategyType(value = SingleStateStrategy.class)
public interface SearchChatView extends MvpView {

}

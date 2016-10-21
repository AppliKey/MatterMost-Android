package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * @author Anatoliy Chub
 */
@StateStrategyType(value = SkipStrategy.class)
public interface SearchChatView extends MvpView {

}

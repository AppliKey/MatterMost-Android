package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * This is view which may produce to "make favorite" command
 */
//TODO Fix naming
@StateStrategyType(value = SingleStateStrategy.class)
public interface FavoriteableView {

    void onMakeFavorite(boolean favorite);
}

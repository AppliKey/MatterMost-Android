package com.applikey.mattermost.mvp.views;

import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface FavoriteableView {

    void onMakeFavorite(boolean favorite);
}

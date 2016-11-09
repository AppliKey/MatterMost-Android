package com.applikey.mattermost.events;

import com.applikey.mattermost.views.SearchTabBehavior;

public class SearchTabIndicatorRequested {

    private final SearchTabBehavior behavior;
    private final boolean visible;

    public SearchTabIndicatorRequested(SearchTabBehavior behavior, boolean visible) {
        this.behavior = behavior;
        this.visible = visible;
    }

    public SearchTabBehavior getBehavior() {
        return behavior;
    }

    public boolean isVisible() {
        return visible;
    }
}

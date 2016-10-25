package com.applikey.mattermost.events;

import com.applikey.mattermost.views.TabBehavior;

public class TabIndicatorRequested {

    private final TabBehavior behavior;
    private final boolean visible;

    public TabIndicatorRequested(TabBehavior behavior, boolean visible) {
        this.behavior = behavior;
        this.visible = visible;
    }

    public TabBehavior getBehavior() {
        return behavior;
    }

    public boolean isVisible() {
        return visible;
    }
}

package com.applikey.mattermost.events;

import com.applikey.mattermost.fragments.BaseChatListFragment;

public class TabIndicatorRequested {

    private final BaseChatListFragment.TabBehavior behavior;
    private final boolean visible;

    public TabIndicatorRequested(BaseChatListFragment.TabBehavior behavior, boolean visible) {
        this.behavior = behavior;
        this.visible = visible;
    }

    public BaseChatListFragment.TabBehavior getBehavior() {
        return behavior;
    }

    public boolean isVisible() {
        return visible;
    }
}

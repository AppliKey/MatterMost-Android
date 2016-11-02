package com.applikey.mattermost.events;

public class UnreadTabStateChangedEvent {

    private final boolean mUnreadTabState;

    public UnreadTabStateChangedEvent(boolean state) {
        mUnreadTabState = state;
    }

    public boolean getUnreadTabState() {
        return mUnreadTabState;
    }
}

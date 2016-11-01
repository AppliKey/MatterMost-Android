package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

public class UserPendingInvitation {

    private User user;
    private boolean isInvited;

    public UserPendingInvitation(User user, boolean isInvited) {
        this.user = user;
        this.isInvited = isInvited;
    }

    public User getUser() {
        return user;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

}

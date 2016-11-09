package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

public class UserPendingInvitation implements Comparable<UserPendingInvitation> {

    private User user;
    private boolean isInvited;

    public UserPendingInvitation(User user, boolean isInvited) {
        this.user = user;
        this.isInvited = isInvited;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    @Override
    public int compareTo(UserPendingInvitation o) {
        return this.user.compareTo(o.getUser());
    }
}

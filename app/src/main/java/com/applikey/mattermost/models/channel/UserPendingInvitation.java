package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

import timber.log.Timber;

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

    public boolean isInvited() {
        return isInvited;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    @Override
    public int compareTo(UserPendingInvitation o) {
        if (this == o) return 0;
        final User thisUser = this.getUser();
        final User otherUser = o.getUser();
        if (thisUser == otherUser) return 0;
        final String thisUserName = User.getDisplayableName(thisUser);
        final String otherUserName = User.getDisplayableName(otherUser);

//        Timber.d(thisUserName + " : " + otherUserName + " : " + thisUserName.compareTo(otherUserName));
        Timber.d("%s : %s : %d", thisUserName, otherUserName, thisUserName.compareTo(otherUserName));

        return thisUserName.compareTo(otherUserName);
    }
}

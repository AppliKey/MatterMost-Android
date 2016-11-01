package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

public class AddedUser {
    private CreatedChannel createdChannel;
    private User user;

    public AddedUser(CreatedChannel createdChannel, User user) {
        this.createdChannel = createdChannel;
        this.user = user;
    }

    public CreatedChannel getCreatedChannel() {
        return createdChannel;
    }

    public User getUser() {
        return user;
    }
}

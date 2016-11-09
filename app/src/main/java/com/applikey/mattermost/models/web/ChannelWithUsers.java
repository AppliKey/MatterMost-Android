package com.applikey.mattermost.models.web;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;

import java.util.List;

public final class ChannelWithUsers {

    private final Channel channel;

    private final List<User> users;

    public ChannelWithUsers(Channel channel,
                            List<User> users) {
        this.channel = channel;
        this.users = users;
    }

    public Channel getChannel() {
        return channel;
    }

    public List<User> getUsers() {
        return users;
    }
}

package com.applikey.mattermost.models.post;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;

public class Message {

    private Channel channel;

    private Post post;

    private User user;

    public Message(Post post, Channel channel) {
        this.channel = channel;
        this.post = post;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int compareByDate(Message item) {
        final long lastPost1 = getPost().getCreatedAt();
        final long lastPost2 = item.getPost().getCreatedAt();
        return (int) (lastPost2 - lastPost1);
    }

    @Override
    public String toString() {
        return channel.toString() + post.toString();
    }
}

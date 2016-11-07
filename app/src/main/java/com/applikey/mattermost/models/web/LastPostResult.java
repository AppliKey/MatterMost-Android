package com.applikey.mattermost.models.web;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;

public class LastPostResult {

    private Channel channel;
    private Post lastPost;

    public LastPostResult(Channel channel, Post post) {
        this.channel = channel;
        this.lastPost = post;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(Post lastPost) {
        this.lastPost = lastPost;
    }
}

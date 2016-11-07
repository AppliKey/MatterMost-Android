package com.applikey.mattermost.models.web;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;

import java.util.List;

public class LastPostResult {

    private Channel channel;
    private List<Post> posts;

    public LastPostResult(Channel channel, List<Post> posts) {
        this.channel = channel;
        this.posts = posts;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}

package com.applikey.mattermost.models.post;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;

/**
 * @author Anatoliy Chub
 */

public class Message implements SearchItem{

    private Channel channel;

    private Post post;

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

    @Override
    public int getSearchType() {
        return Channel.ChannelType.fromRepresentation(channel.getType()) == Channel.ChannelType.DIRECT ? MESSAGE : MESSAGE_CHANNEL;
    }

    @Override
    public String toString() {
        return channel.toString() + post.toString();
    }
}

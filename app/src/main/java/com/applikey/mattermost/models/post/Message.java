package com.applikey.mattermost.models.post;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;

import static com.applikey.mattermost.models.SearchItem.Type.MESSAGE;
import static com.applikey.mattermost.models.SearchItem.Type.MESSAGE_CHANNEL;

public class Message implements SearchItem {

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

    @Override
    public Type getSearchType() {
        return Channel.ChannelType.fromRepresentation(channel.getType())
                == Channel.ChannelType.DIRECT ? MESSAGE : MESSAGE_CHANNEL;
    }

    @Override
    public int getSortPriority() {
        return PRIORITY_MESSAGE;
    }

    @Override
    public int compareByDate(SearchItem item) {
        final int priorityDifference = item.getSortPriority() - this.getSortPriority();

        if (priorityDifference != 0) {
            return priorityDifference;
        }
        long lastPost1 = 0L;
        long lastPost2 = 0L;
        final Message message1 = this;
        final Message message2 = (Message) item;
        lastPost1 = message1.getChannel().getLastPostAt();
        lastPost2 = message2.getChannel().getLastPostAt();
        if (lastPost1 == 0 || lastPost2 == 0) {
            lastPost2 = message2.getChannel().getCreatedAt();
            lastPost1 = message1.getChannel()
                    .getCreatedAt();
        }

        return (int) (lastPost2 - lastPost1);
    }

    @Override
    public String toString() {
        return channel.toString() + post.toString();
    }
}

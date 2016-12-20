package com.applikey.mattermost.models;

import android.support.annotation.StringRes;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.user.User;

import static com.applikey.mattermost.models.SearchItem.Type.CHANNEL;
import static com.applikey.mattermost.models.SearchItem.Type.MESSAGE;
import static com.applikey.mattermost.models.SearchItem.Type.MESSAGE_CHANNEL;

public class SearchItem {

    public static int PRIORITY_USER = 0;

    public static int PRIORITY_MESSAGE = 1;

    public static int PRIORITY_CHANNEL = 2;

    private Message mMessage;

    private User mUser;

    private Channel mChannel;

    private Type mType;

    private int mPriority;

    public SearchItem(Channel channel) {
        mChannel = channel;
        mType = Type.CHANNEL;
        mPriority = PRIORITY_CHANNEL;
    }

    public SearchItem(User user) {
        mUser = user;
        mType = Type.USER;
        mPriority = PRIORITY_USER;
    }

    public SearchItem(Message message) {
        mMessage = message;
        mType = (Channel.ChannelType.fromRepresentation(message.getChannel().getType())
                == Channel.ChannelType.DIRECT) ? MESSAGE : MESSAGE_CHANNEL;
        mPriority = PRIORITY_MESSAGE;
    }

    public enum Type {
        CHANNEL(R.string.header_channel),
        USER(R.string.header_people),
        MESSAGE(R.string.header_message),
        MESSAGE_CHANNEL(R.string.header_message);

        public int getRes() {
            return res;
        }

        private int res;

        Type(@StringRes int res) {
            this.res = res;
        }
    }

    public Type getSearchType() {
        return mType;
    }

    public int getSortPriority() {
        return mPriority;
    }

    public Message getMessage() {
        return mMessage;
    }

    public User getUser() {
        return mUser;
    }

    public Channel getChannel() {
        return mChannel;
    }

    public int compareByDate(SearchItem item) {
        final int priorityDifference = item.getSortPriority() - this.getSortPriority();

        if (priorityDifference != 0) {
            return priorityDifference;
        }

        if (mType.equals(CHANNEL)) {
            return mChannel.compareByDate(item.getChannel());
        } else if (mType.equals(MESSAGE) || mType.equals(MESSAGE_CHANNEL)) {
            return mMessage.compareByDate(item.getMessage());
        }

        return 0;
    }
}

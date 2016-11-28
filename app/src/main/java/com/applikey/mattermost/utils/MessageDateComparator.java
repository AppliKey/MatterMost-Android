package com.applikey.mattermost.utils;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.user.User;

import java.util.Comparator;

/**
 * @author Anatoliy Chub
 */

public class MessageDateComparator implements Comparator<SearchItem> {

    @Override
    public int compare(SearchItem o1, SearchItem o2) {
        Long lastPost1 = 0L;
        Long lastPost2 = 0L;
        if (o1 instanceof Message && o2 instanceof Message) {
            final Message message1 = (Message) o1;
            final Message message2 = (Message) o2;
            lastPost1 = message1.getChannel().getLastPostAt();
            lastPost2 = message2.getChannel().getLastPostAt();
            if (lastPost1 == 0 || lastPost2 == 0) {
                lastPost2 = message2.getChannel().getCreatedAt();
                lastPost1 = message1.getChannel()
                        .getCreatedAt();
            }
        } else if (o1 instanceof Channel && o2 instanceof Message) {
            final Channel channel1 = (Channel) o1;
            final Message message2 = (Message) o2;
            lastPost1 = channel1.getLastPostAt();
            lastPost2 = message2.getChannel().getLastPostAt();
            if (lastPost1 == 0 || lastPost2 == 0) {
                lastPost2 = message2.getChannel().getCreatedAt();
                lastPost1 = channel1.getCreatedAt();
            }
        } else if (o1 instanceof Message && o2 instanceof Channel) {
            final Message message1 = (Message) o1;
            final Channel channel2 = (Channel) o2;
            lastPost1 = message1.getChannel().getLastPostAt();
            lastPost2 = channel2.getLastPostAt();
            if (lastPost1 == 0 || lastPost2 == 0) {
                lastPost2 = channel2.getCreatedAt();
                lastPost1 = message1.getChannel()
                        .getCreatedAt();
            }
        } else if (o1 instanceof Channel && o2 instanceof Channel) {
            final Channel channel1 = (Channel) o1;
            final Channel channel2 = (Channel) o2;
            lastPost1 = channel1.getLastPostAt();
            lastPost2 = channel2.getLastPostAt();
        } else if (o1 instanceof User && !(o2 instanceof User)) {
            return Integer.MAX_VALUE;
        } else if (o2 instanceof User && !(o1 instanceof User)) {
            return Integer.MIN_VALUE;
        }

        return lastPost2.compareTo(lastPost1);
    }
}

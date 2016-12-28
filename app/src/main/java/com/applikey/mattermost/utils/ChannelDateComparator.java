package com.applikey.mattermost.utils;

import com.applikey.mattermost.models.channel.Channel;

import java.util.Comparator;

public class ChannelDateComparator implements Comparator<Channel> {

    @Override
    public int compare(Channel channel1, Channel channel2) {
        final Long lastPost1 = channel1.getLastPostAt();
        final Long lastPost2 = channel2.getLastPostAt();
        return lastPost2.compareTo(lastPost1);
    }
}

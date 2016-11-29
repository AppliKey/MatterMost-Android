package com.applikey.mattermost.utils;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;

import java.util.Comparator;

public class ChannelDateComparator implements Comparator<SearchItem> {

    @Override
    public int compare(SearchItem o1, SearchItem o2) {
        final Channel channel1 = (Channel) o1;
        final Channel channel2 = (Channel) o2;
        final Long lastPost1 = channel1.getLastPostAt();
        final Long lastPost2 = channel2.getLastPostAt();
        return lastPost2.compareTo(lastPost1);
    }
}

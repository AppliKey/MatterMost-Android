package com.applikey.mattermost.models.channel;

import java.util.HashMap;

public class ChannelsWithMetadata extends HashMap<String, ChannelWithMetadata> {

    public ChannelsWithMetadata(int capacity) {
        super(capacity);
    }
}

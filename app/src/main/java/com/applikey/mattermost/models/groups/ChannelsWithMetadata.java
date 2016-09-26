package com.applikey.mattermost.models.groups;

import com.applikey.mattermost.data.MutableTuple;

import java.util.HashMap;

public class ChannelsWithMetadata extends HashMap<String, ChannelWithMetadata> {

    public ChannelsWithMetadata(int capacity) {
        super(capacity);
    }
}

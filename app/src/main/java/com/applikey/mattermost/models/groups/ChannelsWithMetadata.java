package com.applikey.mattermost.models.groups;

import com.applikey.mattermost.data.MutableTuple;

import java.util.HashMap;

public class ChannelsWithMetadata extends HashMap<String, MutableTuple<Channel, Membership>> {

    public ChannelsWithMetadata(int capacity) {
        super(capacity);
    }
}

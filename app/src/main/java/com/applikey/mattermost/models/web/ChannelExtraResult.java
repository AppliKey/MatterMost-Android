package com.applikey.mattermost.models.web;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ExtraInfo;

public final class ChannelExtraResult {

    private final Channel channel;

    private final ExtraInfo extraInfo;

    public ChannelExtraResult(Channel channel,
                              ExtraInfo extraInfo) {
        this.channel = channel;
        this.extraInfo = extraInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

}

package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

public class ChannelPurposeRequest {

    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("channel_purpose")
    private String channelPurpose;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelPurpose() {
        return channelPurpose;
    }

    public void setChannelPurpose(String channelPurpose) {
        this.channelPurpose = channelPurpose;
    }
}

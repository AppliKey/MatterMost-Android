package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

public class ChannelPurposeRequest {

    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("channel_purpose")
    private String channelPurpose;

    public ChannelPurposeRequest(String channelId, String channelPurpose) {
        this.channelId = channelId;
        this.channelPurpose = channelPurpose;
    }

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

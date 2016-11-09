package com.applikey.mattermost.models.socket;

import com.google.gson.annotations.SerializedName;

public class MessagePostedEventData {

    @SerializedName("channel_display_name")
    private String channelDisplayName;

    @SerializedName("channel_type")
    private String channelType;

    @SerializedName("sender_name")
    private String senderName;

    @SerializedName("teamId")
    private String teamId;

    @SerializedName("post")
    private String postObject;

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getPostObject() {
        return postObject;
    }
}

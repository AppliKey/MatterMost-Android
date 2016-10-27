package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

public class ChannelRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("type")
    private String type;

    public ChannelRequest(String name, String purpose, String type) {
        this.name = name;
        this.displayName = name;
        this.purpose = purpose;
        this.type = type;
    }
}

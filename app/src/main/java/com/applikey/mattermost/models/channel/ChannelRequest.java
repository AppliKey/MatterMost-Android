package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

public class ChannelRequest {

    @SerializedName("name")
    private final String name;

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("purpose")
    private final String purpose;

    @SerializedName("type")
    private final String type;

    public ChannelRequest(String name, String purpose, String type) {
        this.name = name.toLowerCase().replaceAll("\\s+", "-");
        this.displayName = name;
        this.purpose = purpose;
        this.type = type;
    }
}

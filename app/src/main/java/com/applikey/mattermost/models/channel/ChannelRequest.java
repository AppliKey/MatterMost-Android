package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelRequest {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("type")
    @Expose
    private String type;

    public ChannelRequest(String name, String purpose, String type) {
        this.name = name;
        this.displayName = name;
        this.purpose = purpose;
        this.type = type;
    }
}

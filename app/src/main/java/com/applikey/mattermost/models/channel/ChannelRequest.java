package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelRequest {

    @SerializedName("team_id")
    @Expose
    private String teamId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("header")
    @Expose
    private String header;
    @SerializedName("type")
    @Expose
    private String type;

    public ChannelRequest(String teamId, String name, String displayName, String purpose, String header, String type) {
        this.teamId = teamId;
        this.name = name;
        this.displayName = displayName;
        this.purpose = purpose;
        this.header = header;
        this.type = type;
    }
}

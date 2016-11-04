package com.applikey.mattermost.models.socket;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class WebSocketEvent {

    @SerializedName("string_id")
    private String teamId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("event")
    private String event;

    // Mattermost v3.2 backport
    @SerializedName("action")
    private String action;

    /**
     * Serialized event data as json
     */
    @SerializedName("data")
    private JsonObject data;

    @SerializedName("props")
    private JsonObject props;

    public String getTeamId() {
        return teamId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEvent() {
        return event;
    }

    public String getAction() {
        return action;
    }

    public JsonObject getData() {
        return data;
    }

    public JsonObject getProps() {
        return props;
    }
}

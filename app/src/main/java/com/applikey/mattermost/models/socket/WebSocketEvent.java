package com.applikey.mattermost.models.socket;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class WebSocketEvent {

    public static final String EVENT_STATUS_CHANGE = "status_change";
    public static final String EVENT_TYPING = "typing";
    public static final String EVENT_CHANNEL_VIEWED = "channel_viewed";
    public static final String EVENT_CHANNEL_DELETED = "channel_deleted";
    public static final String EVENT_USER_ADDED = "user_added";
    public static final String EVENT_USER_REMOVED = "user_removed";
    public static final String EVENT_POST_EDITED = "post_edited";
    public static final String EVENT_POST_DELETED = "post_deleted";
    public static final String EVENT_POST_POSTED = "posted";

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
        // Mattermost Old API fix
        return event == null ? action : event;
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

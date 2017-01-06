package com.applikey.mattermost.models.socket;

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

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("event")
    private String event;

    @SerializedName("props")
    private Props props;

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEvent(String event) {
        this.event = event;
    }

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

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }
}

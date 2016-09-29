package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Membership extends RealmObject {

    @PrimaryKey
    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("user_id")
    private String userId;

    // TODO Migrate data type
    @SerializedName("roles")
    private String roles;

    @SerializedName("last_viewed_at")
    private long lastViewedAt;

    @SerializedName("msg_count")
    private long messageCount;

    @SerializedName("mention_count")
    private long mentionCount;

    @SerializedName("last_update_at")
    private long lastUpdateAt;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    public long getMentionCount() {
        return mentionCount;
    }

    public void setMentionCount(long mentionCount) {
        this.mentionCount = mentionCount;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }
}

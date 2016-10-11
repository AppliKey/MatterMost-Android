package com.applikey.mattermost.models.post;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {

    public static final String FIELD_NAME_CHANNEL_ID = "channelId";

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("crated_at")
    private long createdAt;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("message")
    private String message;

    // Application-specific fields
    private int priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static final Comparator<Post> COMPARATOR_BY_PRIORITY = (o1, o2)
            -> o1.getPriority() - o2.getPriority();
}

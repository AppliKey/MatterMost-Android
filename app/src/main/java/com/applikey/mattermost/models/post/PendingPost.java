package com.applikey.mattermost.models.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingPost {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("create_at")
    @Expose
    private long createdAt;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("channel_id")
    @Expose
    private String channelId;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("pending_post_id")
    @Expose
    private String pendingPostId;

    public String getId() {
        return id;
    }

    public PendingPost() {
    }

    public PendingPost(String id, long createdAt, String userId, String channelId, String message, String type, String pendingPostId) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
        this.type = type;
        this.pendingPostId = pendingPostId;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPendingPostId() {
        return pendingPostId;
    }

    public void setPendingPostId(String pendingPostId) {
        this.pendingPostId = pendingPostId;
    }

    public Post toPost() {
        final Post post = new Post();
        post.setId(id);
        post.setChannelId(channelId);
        post.setCreatedAt(createdAt);
        post.setMessage(message);
        post.setUserId(userId);
        return post;
    }
}

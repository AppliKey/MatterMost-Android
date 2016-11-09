package com.applikey.mattermost.models.post;

import com.google.gson.annotations.SerializedName;

public class PendingPost {

    @SerializedName("id")
    private String id;

    @SerializedName("create_at")
    private long createdAt;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("pending_post_id")
    private String pendingPostId;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("root_id")
    private String rootId;

    public PendingPost() {
    }

    public PendingPost(long createdAt, String userId, String channelId, String message, String type,
            String pendingPostId) {
        this.createdAt = createdAt;
        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
        this.type = type;
        this.pendingPostId = pendingPostId;
    }

    public PendingPost(long createdAt, String userId, String channelId, String message, String type,
            String pendingPostId, String parentId, String rootId) {
        this(createdAt, userId, channelId, message, type, pendingPostId);
        this.parentId = parentId;
        this.rootId = rootId;
    }

    public String getId() {
        return id;
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

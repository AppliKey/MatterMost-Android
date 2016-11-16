package com.applikey.mattermost.models.post;

import android.support.annotation.Nullable;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.user.User;
import com.google.gson.annotations.SerializedName;
import com.vdurmont.emoji.EmojiParser;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject implements SearchItem{

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_CHANNEL_ID = "channelId";
    public static final String FIELD_NAME_CHANNEL_CREATE_AT = "createdAt";
    public static final Comparator<Post> COMPARATOR_BY_PRIORITY = (o1, o2)
            -> o2.getPriority() - o1.getPriority();
    @PrimaryKey
    @SerializedName("id")
    private String id;
    @SerializedName("channel_id")
    private String channelId;
    @Nullable
    @SerializedName("root_id")
    private String rootId;
    @Nullable
    private Post rootPost;
    @Nullable
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("create_at")
    private long createdAt;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("message")
    private String message;
    // Application-specific fields
    private int priority;
    private User author;

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
        return EmojiParser.parseToUnicode(message);
    }

    public void setMessage(String message) {
        this.message = EmojiParser.parseToAliases(message);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Nullable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(@Nullable String parentId) {
        this.parentId = parentId;
    }

    @Nullable
    public String getRootId() {
        return rootId;
    }

    public void setRootId(@Nullable String rootId) {
        this.rootId = rootId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Nullable
    public Post getRootPost() {
        return rootPost;
    }

    public void setRootPost(@Nullable Post rootPost) {
        this.rootPost = rootPost;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getChannelId().hashCode();
        result = 31 * result + (int) (getCreatedAt() ^ (getCreatedAt() >>> 32));
        result = 31 * result + getUserId().hashCode();
        result = 31 * result + getMessage().hashCode();
        result = 31 * result + getPriority();
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Post post = (Post) o;

        if (getCreatedAt() != post.getCreatedAt())
            return false;
        if (getParentId() != null && !getParentId().equals(post.getParentId()))
            return false;
        if (getRootId() != null && !getRootId().equals(post.getRootId()))
            return false;
        if (getPriority() != post.getPriority())
            return false;
        if (!getId().equals(post.getId()))
            return false;
        if (!getChannelId().equals(post.getChannelId()))
            return false;
        if (!getUserId().equals(post.getUserId()))
            return false;
        if (!getMessage().equals(post.getMessage()))
            return false;
        return getAuthor() != null
                ? getAuthor().equals(post.getAuthor())
                : post.getAuthor() == null;
    }

    @Override
    public int getSearchType() {
        return MESSAGE;
    }
}

package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import javax.annotation.Nullable;

import io.realm.DiffEquals;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject implements DiffEquals<Channel> {

    public static final Comparator<Channel> COMPARATOR_BY_DATE = new ComparatorByDate();
    public static final String FIELD_NAME_TYPE = "type";
    public static final String FIELD_UNREAD_TYPE = "hasUnreadMessages";
    public static final String FIELD_NAME_LAST_POST_AT = "lastPostAt";
    public static final String FIELD_NAME_CREATED_AT = "createdAt";
    public static final String FIELD_NAME_LAST_ACTIVITY_TIME = "lastActivityTime";

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName(FIELD_NAME_TYPE)
    private String type;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("name")
    private String name;

    @SerializedName("header")
    private String header;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("last_post_at")
    private long lastPostAt;

    @SerializedName("create_at")
    private long createdAt;

    // Only available for direct channels
    // Another collocutor of a direct chat. This field is used for determining another person in direct chat (except current user)
    private User directCollocutor;

    // Migrated from channel membership
    private long lastViewedAt;

    // Field, which represents the comparison of two fields. Please see https://github.com/realm/realm-java/issues/1615
    private boolean hasUnreadMessages;

    private Post lastPost;

    // Index field, which contains the time of the last message or creation time. Used by Realm, as it can not compare multiple fields
    private long lastActivityTime;

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public void updateLastActivityTime() {
        this.lastActivityTime = Math.max(createdAt, lastPost != null ? lastPost.getCreatedAt() : 0);
    }

    public User getDirectCollocutor() {
        return directCollocutor;
    }

    public void setDirectCollocutor(User directCollocutor) {
        this.directCollocutor = directCollocutor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public long getLastPostAt() {
        return lastPostAt;
    }

    public void setLastPostAt(long lastPostAt) {
        this.lastPostAt = lastPostAt;

        rebuildHasUnreadMessages();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;

        rebuildHasUnreadMessages();
    }

    public boolean hasUnreadMessages() {
        return hasUnreadMessages;
    }

    public void setHasUnreadMessages(boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
    }

    @Nullable
    public Post getLastPost() {
        return lastPost;
    }

    public void setLastPost(@Nullable Post lastPost) {
        this.lastPost = lastPost;
    }

    private void rebuildHasUnreadMessages() {
        final long lastViewedAt = getLastViewedAt();
        final long lastPostAt = getLastPostAt();

        hasUnreadMessages = lastPostAt > lastViewedAt;
    }

    @Override
    public boolean diffEquals(Channel o) {
        return this.getId().equals(o.getId());
    }

    public enum ChannelType {
        PUBLIC("O"),
        PRIVATE("P"),
        DIRECT("D");

        private final String representation;

        ChannelType(String representation) {
            this.representation = representation;
        }

        public String getRepresentation() {
            return representation;
        }

        public static ChannelType fromRepresentation(String representation) {
            switch (representation) {
                case "O": {
                    return PUBLIC;
                }
                case "P": {
                    return PRIVATE;
                }
                case "D": {
                    return DIRECT;
                }
                default: {
                    throw new IllegalArgumentException("Wrong channel type");
                }
            }
        }
    }

    private static class ComparatorByDate implements Comparator<Channel> {

        @Override
        public int compare(Channel o1, Channel o2) {
            final long o1time = o1.getLastPostAt() != 0 ? o1.getLastPostAt() : o1.getCreatedAt();
            final long o2time = o2.getLastPostAt() != 0 ? o2.getLastPostAt() : o2.getCreatedAt();

            if (o1time > o2time) {
                return -1;
            }
            if (o1time == o2time) {
                return 0;
            }
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Channel channel = (Channel) o;

        if (getLastPostAt() != channel.getLastPostAt())
            return false;
        if (getCreatedAt() != channel.getCreatedAt())
            return false;
        if (getLastViewedAt() != channel.getLastViewedAt())
            return false;
        if (hasUnreadMessages() != channel.hasUnreadMessages())
            return false;
        if (getLastActivityTime() != channel.getLastActivityTime())
            return false;
        if (!getId().equals(channel.getId()))
            return false;
        if (!getType().equals(channel.getType()))
            return false;
        if (getDisplayName() != null ? !getDisplayName().equals(channel.getDisplayName()) : channel.getDisplayName() != null)
            return false;
        if (!getName().equals(channel.getName()))
            return false;
        if (!getHeader().equals(channel.getHeader()))
            return false;
        if (!getPurpose().equals(channel.getPurpose()))
            return false;
        if (getDirectCollocutor() != null ? !getDirectCollocutor().equals(channel.getDirectCollocutor()) : channel.getDirectCollocutor() != null)
            return false;
        return getLastPost() != null ? !getLastPost().equals(channel.getLastPost()) : channel.getLastPost() != null;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + getName().hashCode();
        result = 31 * result + getHeader().hashCode();
        result = 31 * result + getPurpose().hashCode();
        result = 31 * result + (int) (getLastPostAt() ^ (getLastPostAt() >>> 32));
        result = 31 * result + (int) (getCreatedAt() ^ (getCreatedAt() >>> 32));
        result = 31 * result + (getDirectCollocutor() != null ? getDirectCollocutor().hashCode() : 0);
        result = 31 * result + (int) (getLastViewedAt() ^ (getLastViewedAt() >>> 32));
        result = 31 * result + (hasUnreadMessages() ? 1 : 0);
        result = 31 * result + (getLastPost() != null ? getLastPost().hashCode() : 0);
        result = 31 * result + (int) (getLastActivityTime() ^ (getLastActivityTime() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", displayName='" + getDisplayName() + '\'' +
                ", name='" + getName() + '\'' +
                ", header='" + getHeader() + '\'' +
                ", purpose='" + getPurpose() + '\'' +
                ", lastPostAt=" + getLastPostAt() +
                ", createdAt=" + getCreatedAt() +
                ", directCollocutor=" + getDirectCollocutor() +
                ", lastViewedAt=" + getLastViewedAt() +
                ", hasUnreadMessages=" + hasUnreadMessages() +
                ", lastPost=" + getLastPost() +
                ", lastActivityTime=" + getLastActivityTime() +
                '}';
    }
}

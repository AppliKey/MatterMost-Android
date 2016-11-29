package com.applikey.mattermost.models.channel;

import android.util.Log;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import rx.Observable;

public class Channel extends RealmObject implements SearchItem {

    public static final Comparator<Channel> COMPARATOR_BY_DATE = new ComparatorByDate();

    public static final String FIELD_NAME_TYPE = "type";
    public static final String FIELD_UNREAD_TYPE = "hasUnreadMessages";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ID = "id";
    public static final String IS_FAVORITE = "isFavorite";
    public static final String FIELD_NAME_LAST_POST_AT = "lastPostAt";
    public static final String FIELD_NAME_CREATED_AT = "createdAt";
    public static final String FIELD_NAME_LAST_ACTIVITY_TIME = "lastActivityTime";
    public static final String FIELD_NAME_COLLOCUTOR_ID = "directCollocutor." + User.FIELD_NAME_ID;

    private static final String TAG = Channel.class.getSimpleName();

    @PrimaryKey
    @SerializedName(FIELD_ID)
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

    //if we are fetching not joined channels, we should set it to false
    private boolean isJoined = true;

    // TODO: 04.11.16 NEED DETAILED REVIEW
    // Application-specific fields
    private String previewImagePath;

    // Only available for direct channels
    // Another collocutor of a direct chat. This field is used for determining another person in
    // direct chat (except current user)
    private User directCollocutor;

    // Migrated from channel membership
    private long lastViewedAt;

    // Field, which represents the comparison of two fields. Please see https://github
    // .com/realm/realm-java/issues/1615
    private boolean hasUnreadMessages;

    private Post lastPost;

    // Index field, which contains the time of the last message or creation time. Used by Realm,
    // as it can not compare multiple fields
    private long lastActivityTime;

    private boolean isFavorite;

    private RealmList<User> mUsers = new RealmList<>();

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public void updateLastActivityTime() {
        if (lastPost != null) {
            this.lastPostAt = lastPost.getCreatedAt();
        }
        this.lastActivityTime = Math.max(createdAt, lastPostAt);
        rebuildHasUnreadMessages();
    }

    public boolean isJoined() {
        Log.d(TAG, "isJoined: " + isJoined);
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
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

    public String getPreviewImagePath() {
        return previewImagePath;
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

    public RealmList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int getSearchType() {
        return CHANNEL;
    }

    @Override
    public int getSortPriority() {
        return PRIORITY_CHANNEL;
    }

    @Override
    public int compareByDate(SearchItem item) {
        final int priorityDifference = item.getSortPriority() - this.getSortPriority();

        if (priorityDifference != 0) {
            return priorityDifference;
        }

        long lastPost1 = 0L;
        long lastPost2 = 0L;
        final Channel channel1 = this;
        final Channel channel2 = (Channel) item;
        lastPost1 = channel1.getLastPostAt();
        lastPost2 = channel2.getLastPostAt();
        return (int) (lastPost2 - lastPost1);
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
        result = 31 * result + (getDirectCollocutor() != null
                ? getDirectCollocutor().hashCode()
                : 0);
        result = 31 * result + (int) (getLastViewedAt() ^ (getLastViewedAt() >>> 32));
        result = 31 * result + (hasUnreadMessages() ? 1 : 0);
        result = 31 * result + (getLastPost() != null ? getLastPost().hashCode() : 0);
        result = 31 * result + (int) (getLastActivityTime() ^ (getLastActivityTime() >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Channel channel = (Channel) o;

        if (getLastPostAt() != channel.getLastPostAt()) {
            return false;
        }
        if (getCreatedAt() != channel.getCreatedAt()) {
            return false;
        }
        if (getLastViewedAt() != channel.getLastViewedAt()) {
            return false;
        }
        if (hasUnreadMessages() != channel.hasUnreadMessages()) {
            return false;
        }
        if (getLastActivityTime() != channel.getLastActivityTime()) {
            return false;
        }
        if (!getId().equals(channel.getId())) {
            return false;
        }
        if (!getType().equals(channel.getType())) {
            return false;
        }
        if (getDisplayName() != null
                ? !getDisplayName().equals(channel.getDisplayName())
                : channel.getDisplayName() != null) {
            return false;
        }
        if (!getName().equals(channel.getName())) {
            return false;
        }
        if (!getHeader().equals(channel.getHeader())) {
            return false;
        }
        if (!getPurpose().equals(channel.getPurpose())) {
            return false;
        }
        if (getDirectCollocutor() != null ? !getDirectCollocutor().equals(
                channel.getDirectCollocutor()) : channel.getDirectCollocutor() != null) {
            return false;
        }
        return getLastPost() != null
                ? !getLastPost().equals(channel.getLastPost())
                : channel.getLastPost() != null;
    }

    public static Observable<List<Channel>> getList(Observable<RealmResults<Channel>> observable) {
        return observable.map(Channel::getList);
    }

    public static List<Channel> getList(RealmResults<Channel> realmResults) {

        final List<Channel> channelList = new ArrayList<>();
        for (Channel channel : realmResults) {
            channelList.add(channel);
        }
        return channelList;

    }

/*    @Override
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
    }*/

    private void rebuildHasUnreadMessages() {
        final long lastViewedAt = getLastViewedAt();
        final long lastPostAt = getLastPostAt();

        hasUnreadMessages = lastPostAt > lastViewedAt;
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

}

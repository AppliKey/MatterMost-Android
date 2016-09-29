package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {

    public static final Comparator<Channel> COMPARATOR_BY_DATE = new ComparatorByDate();
    public static final String FIELD_NAME_TYPE = "type";

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

    private String previewImagePath;

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

    public void setPreviewImagePath(String previewImagePath) {
        this.previewImagePath = previewImagePath;
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
}

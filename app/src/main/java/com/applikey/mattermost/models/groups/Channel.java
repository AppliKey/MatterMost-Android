package com.applikey.mattermost.models.groups;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Channel extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName("type")
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

    public enum ChannelType {
        PUBLIC("O"),
        PRIVATE("P");

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
                default: {
                    throw new IllegalArgumentException("Wrong channel type");
                }
            }
        }
    }
}

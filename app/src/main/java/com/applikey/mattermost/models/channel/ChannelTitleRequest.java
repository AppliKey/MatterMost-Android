package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

public class ChannelTitleRequest {

    private String id;

    @SerializedName("create_at")
    private long createAt;

    @SerializedName("update_at")
    private long updateAt;

    @SerializedName("delete_at")
    private long deleteAt;

    @SerializedName("team_id")
    private String teamId;

    private String type;

    @SerializedName("display_name")
    private String displayName;

    private String name;

    private String header;

    private String purpose;

    @SerializedName("last_post_at")
    private long lastPostAt;

    @SerializedName("total_msg_count")
    private long totalMsgCount;

    @SerializedName("extra_update_at")
    private long extraUpdateAt;

    @SerializedName("creator_id")
    private String creatorId;

    public ChannelTitleRequest(Channel channel) {
        this.id = channel.getId();
        this.createAt = channel.getCreatedAt();
        this.type = channel.getType();
        this.displayName = channel.getDisplayName();
        this.name = channel.getName();
        this.header = channel.getHeader();
        this.purpose = channel.getPurpose();
        this.lastPostAt = channel.getLastPostAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public long getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(long deleteAt) {
        this.deleteAt = deleteAt;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
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

    public long getTotalMsgCount() {
        return totalMsgCount;
    }

    public void setTotalMsgCount(long totalMsgCount) {
        this.totalMsgCount = totalMsgCount;
    }

    public long getExtraUpdateAt() {
        return extraUpdateAt;
    }

    public void setExtraUpdateAt(long extraUpdateAt) {
        this.extraUpdateAt = extraUpdateAt;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}

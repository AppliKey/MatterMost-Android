package com.applikey.mattermost.models.socket;

import com.applikey.mattermost.models.post.Post;
import com.google.gson.annotations.SerializedName;

/**
 * Mattermost v3.2 API backport
 */
public class Props {

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("channel_display_name")
    private String channelDisplayName;

    @SerializedName("channel_type")
    private String channelType;

    @SerializedName("sender_name")
    private String senderName;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("post")
    private Post post;

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}

package com.applikey.mattermost.models.channel;

public class CreatedChannel {
    private String teamId;
    private String channelId;

    public CreatedChannel(String teamId, String channelId) {
        this.teamId = teamId;
        this.channelId = channelId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getChannelId() {
        return channelId;
    }
}

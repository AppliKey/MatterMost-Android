package com.applikey.mattermost.models.channel;

public class CreatedChannel {

    private final String teamId;
    private final Channel channel;

    public CreatedChannel(String teamId, Channel channel) {
        this.teamId = teamId;
        this.channel = channel;
    }

    public String getTeamId() {
        return teamId;
    }

    public Channel getChannel() {
        return channel;
    }
}

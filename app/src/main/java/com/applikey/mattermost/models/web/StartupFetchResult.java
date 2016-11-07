package com.applikey.mattermost.models.web;

import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.user.User;

import java.util.Map;

public class StartupFetchResult {

    private ChannelResponse channelResponse;
    private Map<String, User> directProfiles;
    private String teamId;

    public StartupFetchResult(ChannelResponse channelResponse,
            Map<String, User> directProfiles,
            String teamId) {
        this.channelResponse = channelResponse;
        this.directProfiles = directProfiles;
        this.teamId = teamId;
    }

    public ChannelResponse getChannelResponse() {
        return channelResponse;
    }

    public Map<String, User> getDirectProfiles() {
        return directProfiles;
    }

    public String getTeamId() {
        return teamId;
    }
}

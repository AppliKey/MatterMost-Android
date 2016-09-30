package com.applikey.mattermost.models.groups;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ChannelResponse {

    @SerializedName("channels")
    private List<Channel> channels;

    @SerializedName("members")
    private Map<String, Membership> membershipEntries;

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public Map<String, Membership> getMembershipEntries() {
        return membershipEntries;
    }

    public void setMembershipEntries(Map<String, Membership> membershipEntries) {
        this.membershipEntries = membershipEntries;
    }
}

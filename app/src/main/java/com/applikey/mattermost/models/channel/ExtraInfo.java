package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExtraInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("members")
    private List<MemberInfo> members;

    @SerializedName("members_count")
    private int membersCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MemberInfo> getMembers() {
        return members;
    }

    public void setMembers(List<MemberInfo> members) {
        this.members = members;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }
}

package com.applikey.mattermost.models.commands;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InviteNewMembersRequest {

    @SerializedName("invites")
    private List<Invite> invites;

    public InviteNewMembersRequest(Invite invite) {
        invites = new ArrayList<>();
        invites.add(invite);
    }
}

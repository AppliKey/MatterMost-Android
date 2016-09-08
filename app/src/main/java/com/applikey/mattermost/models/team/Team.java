package com.applikey.mattermost.models.team;

import com.google.gson.annotations.SerializedName;

public class Team {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("displayName")
    String displayName;
}

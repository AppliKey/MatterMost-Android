package com.applikey.mattermost.models.commands;

import com.google.gson.annotations.SerializedName;

public class Invite {

    @SerializedName("email")
    private String email;

    //@Shame("Mattermost Team", "For using camel case ONCE")
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    public Invite(String email, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}

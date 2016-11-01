package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String userName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    public AuthenticationResponse(String id, String userName, String firstName, String lastName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.SerializedName;

public class AttachDeviceRequest {

    // We always need an android prefix in our case because of API design.
    private static final String PREFIX = "android:";

    @SerializedName("device_id")
    private String deviceId;

    public void setGcmToken(String gcmToken) {
        this.deviceId = PREFIX + gcmToken;
    }
}

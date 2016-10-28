package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.SerializedName;

public class AttachDeviceRequest {
    @SerializedName("device_id")
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

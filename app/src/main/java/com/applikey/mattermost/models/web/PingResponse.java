package com.applikey.mattermost.models.web;

import com.google.gson.annotations.SerializedName;

public class PingResponse {

    @SerializedName("server_time")
    private long serverTime;

    @SerializedName("version")
    private String version;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

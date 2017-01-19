package com.applikey.mattermost.models.file;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadResponse {

    @SerializedName("filenames")
    private List<String> fileName;

    @SerializedName("client_ids")
    private List<String> clientId;

    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }

    public List<String> getClientId() {
        return clientId;
    }

    public void setClientId(List<String> clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "UploadResponse{" +
                "fileName=" + fileName +
                ", clientId=" + clientId +
                '}';
    }
}

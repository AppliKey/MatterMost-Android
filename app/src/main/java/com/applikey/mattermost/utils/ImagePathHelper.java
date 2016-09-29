package com.applikey.mattermost.utils;

import com.applikey.mattermost.web.ServerUrlFactory;

public class ImagePathHelper {

    private final ServerUrlFactory mServerUrlFactory;

    public ImagePathHelper(ServerUrlFactory serverUrlFactory) {
        mServerUrlFactory = serverUrlFactory;
    }

    public String getProfilePicPath(String userId) {
        return mServerUrlFactory.getServerUrl() + "api/v3/users/" + userId + "/image";
    }
}

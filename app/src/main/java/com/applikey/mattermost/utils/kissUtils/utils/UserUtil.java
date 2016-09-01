/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

public class UserUtil {

    public static final String TAG = "UserUtil";

    private UserUtil() {
    }

    private static String userId;

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String id) {
        userId = id;
    }
}

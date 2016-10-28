package com.applikey.mattermost.utils;

import java.util.UUID;

public class IdUtils {

    private IdUtils() {
    }

    public static String newUuid26() {
        final String uuid = UUID.randomUUID().toString(); // 36
        final String truncatedUuid = uuid.replace("-", ""); // 32
        return truncatedUuid.substring(6); // 26
    }
}

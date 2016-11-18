package com.applikey.mattermost;


public class Constants {

    // TODO replace with actual package name
    public static final String PACKAGE_NAME = "com.applikey.mattermost";

    public static final int TIMEOUT_DURATION_SEC = 20;

    // TODO Use server as realm identifier
    public static final String REALM_NAME = "Test1.realm";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String CURRENT_USER_QUALIFIER = "currentUserId";

    public static final String GCM_REGISTRATION_COMPLETE = "registrationComplete";
    public static final String GCM_SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String WEB_SOCKET_ENDPOINT = "api/v3/users/websocket";

    public static final int WEB_SOCKET_TIMEOUT = 5000;

    public static final long INPUT_REQUEST_TIMEOUT_MILLISEC = 500;

    public static final int POLLING_PERIOD_SECONDS = 15;

    public static final String EMPTY_STRING = "";

    public static final String PERSISTENT_PREFS_FILE_NAME = "PersistentPrefs";
}

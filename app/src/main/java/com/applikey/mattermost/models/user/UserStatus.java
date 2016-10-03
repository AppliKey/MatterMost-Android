package com.applikey.mattermost.models.user;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserStatus extends RealmObject {

    @PrimaryKey
    private String id;

    private int status;

    public UserStatus() {
    }

    public UserStatus(String id, String status) {
        this.id = id;
        this.status = Status.from(status).ordinal();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Status getStatusValue() {
        return Status.values()[status];
    }

    public enum Status {
        ONLINE, AWAY, OFFLINE;

        private static final Map<String, Status> representations = new HashMap<String, Status>() {{
            put("online", ONLINE);
            put("away", AWAY);
            put("offline", OFFLINE);
        }};

        public static Status from(String representation) {
            return representations.get(representation);
        }
    }
}

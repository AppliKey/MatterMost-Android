package com.applikey.mattermost.models.user;

import com.applikey.mattermost.R;

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
        OFFLINE(R.drawable.indicator_status_offline),
        ONLINE(R.drawable.indicator_status_online),
        AWAY(R.drawable.indicator_status_idle);

        private final int drawableId;

        Status(int drawableId) {
            this.drawableId = drawableId;
        }

        public int getDrawableId() {
            return drawableId;
        }

        private static final Map<String, Status> representations = new HashMap<String, Status>() {{
            put("offline", OFFLINE);
            put("online", ONLINE);
            put("away", AWAY);
        }};

        public static Status from(String representation) {
            return representations.get(representation);
        }

        public static Status from(int ordinal) {
            return values()[ordinal];
        }
    }
}

package com.applikey.mattermost.models.user;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.applikey.mattermost.R;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Comparable<User>, Searchable<String> {

    public static final String FIELD_NAME_ID = "id";

    public static final String FIELD_USERNAME = "username";

    @PrimaryKey
    @SerializedName(FIELD_NAME_ID)
    private String id;

    @SerializedName(FIELD_USERNAME)
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    // TODO Remove this parameter after migration to 3.4
    @SerializedName("last_activity_at")
    private long lastActivityAt;

    @SerializedName("update_at")
    private long updateAt;

    // Application-specific fields
    private String profileImage;

    private int status;

    public User() {
    }

    public User(String id, String username, String email, String firstName,
                String lastName, long lastActivityAt, long updateAt,
                String profileImage, int status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastActivityAt = lastActivityAt;
        this.updateAt = updateAt;
        this.profileImage = profileImage;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getLastActivityAt() {
        return lastActivityAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public void setLastActivityAt(long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static String getDisplayableName(@NonNull User user) {
        final StringBuilder builder = new StringBuilder();

        if (!user.getFirstName().isEmpty()) {
            builder.append(user.getFirstName());
        }

        if (!user.getLastName().isEmpty()) {
            if (!user.getFirstName().isEmpty()) {
                builder.append(" ");
            }
            builder.append(user.getLastName());
        }

        if (builder.toString().isEmpty()) {
            builder.append(user.getUsername());
        }

        return builder.toString();
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

        private static final Map<String, Status> representations =
                new HashMap<String, Status>() {{
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final User user = (User) o;

        if (!getId().equals(user.getId()))
            return false;
        if (!getUsername().equals(user.getUsername()))
            return false;
        if (!getEmail().equals(user.getEmail()))
            return false;
        if (!getFirstName().equals(user.getFirstName()))
            return false;
        if (!getLastName().equals(user.getLastName()))
            return false;
        return getProfileImage().equals(user.getProfileImage());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + (int) (getLastActivityAt() ^ (getLastActivityAt() >>> 32));
        result = 31 * result + (int) (getUpdateAt() ^ (getUpdateAt() >>> 32));
        result = 31 * result + getProfileImage().hashCode();
        result = 31 * result + getStatus();
        return result;
    }

    @Override
    public int compareTo(@NonNull User o) {
        if (this == o)
            return 0;
        final String thisUserDisplayableNameIgnoreCase = User.getDisplayableName(this).toLowerCase();
        final String otherUserDisplayableNameIgnoreCase = User.getDisplayableName(o).toLowerCase();
        return thisUserDisplayableNameIgnoreCase.compareTo(otherUserDisplayableNameIgnoreCase);
    }

    @Override
    public String toString() {
        return User.getDisplayableName(this);
    }

    @Override
    public boolean search(String searchFilter) {
        if (TextUtils.isEmpty(searchFilter)) {
            return true;
        }
        boolean result = false;

        if (firstName.contains(searchFilter) || lastName.contains(searchFilter) || email.contains(searchFilter)) {
            result = true;
        }
        return result;
    }
}

package com.applikey.mattermost.models.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject
        implements RealmModel, Comparable<User>, Searchable<String>, Parcelable {

    public static final String FIELD_NAME_ID = "id";

    public static final String FIELD_USERNAME = "username";

    public static final String FIRST_NAME = "firstName";

    public static final String LAST_NAME = "lastName";

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

    @SerializedName("create_at")
    private long createAt;

    // Application-specific fields
    private String profileImage;

    private int status;

    public enum Status {
        OFFLINE(R.drawable.indicator_status_offline),
        ONLINE(R.drawable.indicator_status_online),
        AWAY(R.drawable.indicator_status_idle);

        private static final Map<String, Status> representations =
                new HashMap<String, Status>() {{
                    put("offline", OFFLINE);
                    put("online", ONLINE);
                    put("away", AWAY);
                }};

        private final int drawableId;

        Status(int drawableId) {
            this.drawableId = drawableId;
        }

        public int getDrawableId() {
            return drawableId;
        }

        public static Status from(String representation) {
            return representations.get(representation);
        }

        public static Status from(int ordinal) {
            return values()[ordinal];
        }
    }

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

    protected User(Parcel in) {
        id = in.readString();
        username = in.readString();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        lastActivityAt = in.readLong();
        updateAt = in.readLong();
        createAt = in.readLong();
        profileImage = in.readString();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeLong(lastActivityAt);
        dest.writeLong(updateAt);
        dest.writeLong(createAt);
        dest.writeString(profileImage);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public void setLastActivityAt(long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
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

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public static String getDisplayableName(@NonNull User user) {
        if (user == null) {
            return Constants.EMPTY_STRING;
        }

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

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + (int) (getLastActivityAt() ^ (getLastActivityAt() >>> 32));
        result = 31 * result + (int) (getUpdateAt() ^ (getUpdateAt() >>> 32));
        result = 31 * result + (int) (getCreateAt() ^ (getCreateAt() >>> 32));
        result = 31 * result + getStatus();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final User user = (User) o;

        if (!getId().equals(user.getId())) {
            return false;
        }
        if (!getUsername().equals(user.getUsername())) {
            return false;
        }
        if (!getEmail().equals(user.getEmail())) {
            return false;
        }
        if (!getFirstName().equals(user.getFirstName())) {
            return false;
        }
        if (!getLastName().equals(user.getLastName())) {
            return false;
        }
        return getProfileImage().equals(user.getProfileImage());

    }

    @Override
    public int compareTo(@NonNull User o) {
        if (this == o) {
            return 0;
        }
        final String thisUserDisplayableNameIgnoreCase = User.getDisplayableName(this)
                .toLowerCase();
        final String otherUserDisplayableNameIgnoreCase = User.getDisplayableName(o).toLowerCase();
        return thisUserDisplayableNameIgnoreCase.compareTo(otherUserDisplayableNameIgnoreCase);
    }

    @Override
    public boolean search(String searchFilter) {
        if (TextUtils.isEmpty(searchFilter)) {
            return true;
        }
        boolean result = false;

        searchFilter = searchFilter.toLowerCase();

        if (firstName.toLowerCase().contains(searchFilter)
                || lastName.toLowerCase().contains(searchFilter)
                || email.toLowerCase().contains(searchFilter)) {
            result = true;
        }
        return result;
    }

}

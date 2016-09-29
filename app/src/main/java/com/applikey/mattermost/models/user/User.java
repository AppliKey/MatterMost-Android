package com.applikey.mattermost.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName("username")
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

    private String profileImage;

    public User() {
    }

    public User(String id, String username, String email, String firstName,
                String lastName, long lastActivityAt, long updateAt, String profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastActivityAt = lastActivityAt;
        this.updateAt = updateAt;
        this.profileImage = profileImage;
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

    public static String getDisplayableName(User user) {
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

    public final static Comparator<User> COMPARATOR_BY_NAME = (u1, u2) -> {
        final String u1Name = getDisplayableName(u1);
        final String u2Name = getDisplayableName(u2);

        return u1Name.compareTo(u2Name);
    };

}

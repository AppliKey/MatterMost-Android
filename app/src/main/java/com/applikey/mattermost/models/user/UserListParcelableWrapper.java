package com.applikey.mattermost.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class UserListParcelableWrapper implements Parcelable {

    private final List<User> data;

    public UserListParcelableWrapper(List<User> data) {
        this.data = data;
    }

    private UserListParcelableWrapper(Parcel in) {
        data = in.createTypedArrayList(User.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserListParcelableWrapper> CREATOR = new Creator<UserListParcelableWrapper>() {
        @Override
        public UserListParcelableWrapper createFromParcel(Parcel in) {
            return new UserListParcelableWrapper(in);
        }

        @Override
        public UserListParcelableWrapper[] newArray(int size) {
            return new UserListParcelableWrapper[size];
        }
    };

    public List<User> getData() {
        return data;
    }
}

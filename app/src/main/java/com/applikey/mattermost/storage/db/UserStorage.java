package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.image.ImagePathHelper;

import java.util.List;
import java.util.Map;

import rx.Observable;

public class UserStorage {

    private final Db mDb;
    private final ImagePathHelper mImagePathHelper;

    public UserStorage(Db db, ImagePathHelper imagePathHelper) {
        mDb = db;
        mImagePathHelper = imagePathHelper;
    }

    public void saveUsers(Map<String, User> directProfiles, Map<String, String> userStatuses) {
        addImagePathInfo(directProfiles);
        addStatusData(directProfiles, userStatuses);
        mDb.saveTransactionalWithRemoval(directProfiles.values());
    }

    public Observable<List<User>> listDirectProfiles() {
        return mDb.listRealmObjects(User.class);
    }

    public Observable<List<User>> searchUsers(String text){
        return mDb.listRealmObjectsFiltered(User.class, text, new String[]{User.FIRST_NAME, User.LAST_NAME, User.USER_NAME});
    }

    private void addImagePathInfo(Map<String, User> users) {
        for (User user : users.values()) {
            user.setProfileImage(mImagePathHelper.getProfilePicPath(user.getId()));
        }
    }

    private void addStatusData(Map<String, User> directProfiles, Map<String, String> userStatuses) {
        for (User user : directProfiles.values()) {
            final String status = userStatuses.get(user.getId());
            user.setStatus(status != null ? User.Status.from(status).ordinal() :
                    User.Status.OFFLINE.ordinal());
        }
    }
}

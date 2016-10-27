package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;

import java.util.List;
import java.util.Map;

import rx.Observable;

public class UserStorage {

    private final Db mDb;
    private final ImagePathHelper mImagePathHelper;
    private final Prefs mPrefs;

    public UserStorage(Db db, Prefs prefs, ImagePathHelper imagePathHelper) {
        mDb = db;
        mPrefs = prefs;
        mImagePathHelper = imagePathHelper;
    }

    public void saveUsers(Map<String, User> directProfiles) {
        addImagePathInfo(directProfiles);
        mDb.saveTransactional(directProfiles.values());
    }

    public void saveUsersStatuses(Map<String, User> directProfiles, Map<String, String> userStatuses) {
        addStatusData(directProfiles, userStatuses);
        mDb.saveTransactional(directProfiles.values());
    }

    public Observable<List<User>> listDirectProfiles() {
        return mDb.listRealmObjects(User.class);
    }

    public Observable<List<User>> listDirectProfiles(boolean includeCurrentUser) {
        if (includeCurrentUser) {
            return mDb.listRealmObjects(User.class);
        } else {
            String currentUserId = mPrefs.getCurrentUserId();
            return mDb.listRealmObjectsExcluded(User.class, "id", currentUserId);
        }
    }

    public Observable<User> getDirectProfile(String id) {
        return mDb.getObject(User.class, id);
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

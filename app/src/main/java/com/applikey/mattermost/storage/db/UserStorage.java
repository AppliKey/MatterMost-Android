package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Single;

public class UserStorage {

    private final Db mDb;
    private final Prefs mPrefs;
    private final ImagePathHelper mImagePathHelper;

    public UserStorage(Db db, Prefs prefs, ImagePathHelper imagePathHelper) {
        mDb = db;
        mPrefs = prefs;
        mImagePathHelper = imagePathHelper;
    }

    public void saveUsers(Map<String, User> directProfiles) {
        addImagePathInfo(directProfiles);
        mDb.saveTransactional(directProfiles.values());
    }

    public void saveUsersStatuses(Map<String, User> directProfiles,
                                  Map<String, String> userStatuses) {
        addStatusData(directProfiles, userStatuses);
        mDb.saveTransactional(directProfiles.values());
    }

    public void updateUsersStatuses(Map<String, String> usersStatusesMap) {
        mDb.updateMapTransactional(usersStatusesMap, User.class, (user, status, realm) -> {
            if (realm != null && user != null) {
                user.setStatus(status != null ? User.Status.from(status).ordinal() :
                        User.Status.OFFLINE.ordinal());
            }
        });
    }

    public Observable<List<User>> listDirectProfiles() {
        return mDb.listRealmObjects(User.class);
    }

    public Observable<User> getDirectProfile(String id) {
        return mDb.getObject(User.class, id);
    }

    public Observable<User> getUserByUsername(String userName) {
        return mDb.getObjectQualified(User.class, User.FIELD_USERNAME, userName);
    }

    public Observable<List<User>> searchUsers(String text) {
        return mDb.listRealmObjectsFilteredSorted(User.class, text,
                                                  new String[] {User.FIRST_NAME, User.LAST_NAME, User.FIELD_USERNAME},
                                                  User.FIELD_USERNAME);
    }

    public Observable<List<User>> findUsers(List<String> ids) {
        String[] idsArray = new String[ids.size()];
        idsArray = ids.toArray(idsArray);
        return mDb.getObjectsQualifiedWithCopy(User.class, User.FIELD_NAME_ID, idsArray);
    }

    public Single<User> getMe() {
        return mDb.getObject(User.class, mPrefs.getCurrentUserId())
                .toSingle();
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

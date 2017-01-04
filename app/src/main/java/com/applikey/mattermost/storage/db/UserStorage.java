package com.applikey.mattermost.storage.db;

import com.annimon.stream.Stream;
import com.applikey.mattermost.models.channel.Channel;
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

    public void save(Map<String, User> directProfiles) {
        addImagePathInfo(directProfiles);
        mDb.saveTransactional(directProfiles.values());
    }

    public void save(User user) {
        addImagePathInfo(user);
        mDb.saveTransactional(user);
    }

    public void saveUsersStatuses(Map<String, User> directProfiles,
                                  Map<String, String> userStatuses) {
        addStatusData(directProfiles, userStatuses);
        mDb.saveTransactional(directProfiles.values());
    }

    public void updateUsersStatuses(Map<String, String> statusesMap) {
        mDb.updateMapTransactionalSync(statusesMap, User.class, (user, status, realm) -> {
            if (user != null) {
                user.setStatus(status != null
                        ? User.Status.from(status).ordinal()
                        : User.Status.OFFLINE.ordinal());
            }
        });
    }

    public void updateUsersStatusesAsync(Map<String, String> usersStatusesMap) {
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

    public Observable<List<User>> getDirectUsers() {
        return mDb.getCopiedObjects(realm -> realm.where(User.class).findAll());
    }

    public Observable<User> getDirectProfile(String id) {
        return mDb.getObject(User.class, id);
    }

    public Observable<User> getUserByUsername(String userName) {
        return mDb.getObjectQualified(User.class, User.FIELD_USERNAME, userName);
    }

    public Observable<User> getUserByEmail(String email) {
        return mDb.getObjectQualifiedNullable(User.class, User.EMAIL, email)
                .flatMap(user -> user.isValid() ? Observable.just(user) : Observable.just(null));
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

    public Observable<List<User>> getChannelUsers(Channel channel) {
        return Observable.from(channel.getUsers())
                .map(mDb::copyFromRealm)
                .toList();
    }

    public Single<User> getMe() {
        return mDb.getObjectAndCopy(User.class, mPrefs.getCurrentUserId())
                .toSingle();
    }

    private void addImagePathInfo(Map<String, User> users) {
        Stream.of(users).forEach(user -> addImagePathInfo(user.getValue()));
    }

    private void addImagePathInfo(User user) {
        user.setProfileImage(mImagePathHelper.getProfilePicPath(user.getId()));
    }

    private void addStatusData(Map<String, User> directProfiles, Map<String, String> userStatuses) {
        for (User user : directProfiles.values()) {
            final String status = userStatuses.get(user.getId());
            user.setStatus(status != null ? User.Status.from(status).ordinal() :
                    User.Status.OFFLINE.ordinal());
        }
    }
}

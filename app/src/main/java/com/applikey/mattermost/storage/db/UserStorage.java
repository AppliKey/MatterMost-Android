package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

public class UserStorage {

    @Inject
    Db mDb;

    public UserStorage() {
        App.getComponent().inject(this);
    }

    public void saveUsers(Map<String, User> directProfiles) {
        mDb.saveTransactionalWithRemoval(directProfiles.values());
    }

    public Observable<List<User>> listDirectProfiles() {
        return mDb.listRealmObjects(User.class);
    }
}

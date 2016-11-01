package com.applikey.mattermost.storage.db;

import javax.inject.Inject;

public class StorageDestroyer {

    private final Db mDb;

    @Inject
    public StorageDestroyer(Db db) {
        mDb = db;
    }

    public void deleteDatabase() {
        mDb.deleteDatabase();
    }
}

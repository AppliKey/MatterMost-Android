package com.applikey.mattermost.storage.db;

public class StorageDestroyer {

    private Db mDb;

    public StorageDestroyer(Db db) {
        mDb = db;
    }

    public void deleteDatabase() {
        mDb.deleteDatabase();
    }
}

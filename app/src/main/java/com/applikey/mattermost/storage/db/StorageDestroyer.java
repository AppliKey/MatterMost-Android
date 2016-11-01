package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.injects.PerApp;

import javax.inject.Inject;

@PerApp
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

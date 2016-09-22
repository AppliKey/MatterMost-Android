package com.applikey.mattermost.storage.db;

import android.content.Context;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import rx.Observable;

public class Db {

    // TODO Use server as realm identifier
    private static final String REALM_NAME = "Test1.realm";

    private final Executor mWritesExecutor;

    public Db(Context context) {
        mWritesExecutor = Executors.newCachedThreadPool();
        final RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name(REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public void saveTransactional(RealmObject object) {
        final Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        realm.close();
    }

    public void saveTransactionalWithRemoval(RealmObject object) {
        final Realm realm = getRealm();
        realm.beginTransaction();
        realm.delete(object.getClass());
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        realm.close();
    }

    public void saveTransactionalWithRemoval(Iterable<? extends RealmObject> objects) {
        final Iterator<? extends RealmObject> iterator = objects.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        final Realm realm = getRealm();
        realm.beginTransaction();
        final Class<? extends RealmObject> clazz = iterator.next().getClass();
        realm.delete(clazz);
        realm.copyToRealmOrUpdate(objects);
        realm.commitTransaction();
        realm.close();
    }

    public void saveTransactionalWithRemovalAsync(RealmObject object) {
        mWritesExecutor.execute(() -> {
            saveTransactionalWithRemoval(object);
        });
    }

    public void saveTransactional(Iterable<? extends RealmObject> objects) {
        final Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(objects);
        realm.commitTransaction();
        realm.close();
    }

    public void saveTransactionalWithRemovalAsync(Iterable<? extends RealmObject> objects) {
        mWritesExecutor.execute(() -> {
            saveTransactionalWithRemoval(objects);
        });
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjects(Class<T> tClass) {
        final Realm realm = getRealm();
        return realm
                .where(tClass)
                .findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<T> listSingeRealmObject(
            Class<T> tClass,
            String primaryKeyColumnName, String primaryKey) {
        final Realm realm = getRealm();
        return realm
                .where(tClass)
                .equalTo(primaryKeyColumnName, primaryKey)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);
    }

    public Observable<DictionaryEntry> getSingleDictionaryEntry(String key) {
        final Realm realm = getRealm();
        return realm
                .where(DictionaryEntry.class)
                .equalTo("key", key)
                .findFirstAsync()
                .<DictionaryEntry>asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);

    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}

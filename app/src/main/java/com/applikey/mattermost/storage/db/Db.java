package com.applikey.mattermost.storage.db;

import android.content.Context;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import rx.Observable;

public class Db {

    // TODO Use server as realm identifier
    private static final String REALM_NAME = "Test1.realm";
    private static final String TAG = Db.class.getSimpleName();

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
        realm.executeTransactionAsync(bgRealm -> {
            bgRealm.copyToRealmOrUpdate(object);
        }, realm::close);
    }

    public void deleteTransactional(RealmObject realmObject) {
        final Realm realm = getRealm();
        realm.beginTransaction();
        realmObject = realm.copyToRealmOrUpdate(realmObject);
        realmObject.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public void saveTransactionalWithRemoval(RealmObject object) {
        final Realm realm = getRealm();
        realm.executeTransactionAsync(bgRealm -> {
            bgRealm.delete(object.getClass());
            bgRealm.copyToRealmOrUpdate(object);
        }, realm::close);
    }

    public void saveTransactionalWithRemoval(Iterable<? extends RealmObject> objects) {
        final Iterator<? extends RealmObject> iterator = objects.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        final Realm realm = getRealm();
        realm.executeTransactionAsync(bgRealm -> {
            final Class<? extends RealmObject> clazz = iterator.next().getClass();
            bgRealm.delete(clazz);
            bgRealm.copyToRealmOrUpdate(objects);
        }, realm::close);
    }

    @Deprecated
    public void saveTransactionalWithRemovalAsync(RealmObject object) {
        mWritesExecutor.execute(() -> {
            saveTransactionalWithRemoval(object);
        });
    }

    public void saveTransactional(Iterable<? extends RealmObject> objects) {
        final Realm realm = getRealm();
        realm.executeTransaction(bgRealm -> {
            bgRealm.copyToRealmOrUpdate(objects);
        });
    }

    @Deprecated
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

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFiltered(Class<T> tClass,
            String fieldName,
            String value) {
        final Realm realm = getRealm();
        return realm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFilteredSorted(Class<T>
            tClass,
            String fieldName,
            String sortBy,
            String value) {
        final Realm realm = getRealm();
        return realm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(response -> !response.isEmpty())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFiltered(Class<T> tClass,
            String fieldName,
            boolean value) {
        final Realm realm = getRealm();
        return realm
                .where(tClass)
                .equalTo(fieldName, value)
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

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFiltered(Class<T> tClass,
            String text,
            String[] fields) {
        final Realm realm = getRealm();

        RealmQuery<T> query = realm
                .where(tClass);

        query.beginGroup();
        for (int i = 0; i < fields.length; i++) {
            query.contains(fields[i], text);
            if(i + 1 < fields.length){
                query = query.or();
            }
        }
        query.endGroup();

        return query.findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .doOnUnsubscribe(realm::close)
                .map(realm::copyFromRealm);

    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}

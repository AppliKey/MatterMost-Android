package com.applikey.mattermost.storage.db;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

public class Db {

    private final Executor mWritesExecutor;
    private final Realm mRealm;

    public Db(Realm realm) {
        mWritesExecutor = Executors.newCachedThreadPool();
        mRealm = realm;
    }

    public void saveTransactional(RealmObject object) {
        mRealm.executeTransactionAsync(realm -> {
            realm.copyToRealmOrUpdate(object);
        });
    }

    public void deleteTransactional(final RealmObject realmObject) {
        mRealm.executeTransactionAsync(realm -> {
            realm.copyToRealmOrUpdate(realmObject).deleteFromRealm();
        });
    }

    public void saveTransactionalWithRemoval(RealmObject object) {
        mRealm.executeTransactionAsync(realm -> {
            realm.delete(object.getClass());
            realm.copyToRealmOrUpdate(object);
        });
    }

    public void saveTransactionalWithRemoval(Iterable<? extends RealmObject> objects) {
        final Iterator<? extends RealmObject> iterator = objects.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        mRealm.executeTransactionAsync(realm -> {
            final Class<? extends RealmObject> clazz = iterator.next().getClass();
            realm.delete(clazz);
            realm.copyToRealmOrUpdate(objects);
        });
    }

    @Deprecated
    public void saveTransactionalWithRemovalAsync(RealmObject object) {
        mWritesExecutor.execute(() -> {
            saveTransactionalWithRemoval(object);
        });
    }

    public void saveTransactional(Iterable<? extends RealmObject> objects) {
        mRealm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(objects);
        });
    }

    @Deprecated
    public void saveTransactionalWithRemovalAsync(Iterable<? extends RealmObject> objects) {
        mWritesExecutor.execute(() -> {
            saveTransactionalWithRemoval(objects);
        });
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjects(Class<T> tClass) {
        return mRealm
                .where(tClass)
                .findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFiltered(Class<T> tClass,
            String fieldName,
            String value) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFilteredSorted(Class<T>
            tClass,
            String fieldName,
            String sortBy,
            String value) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(response -> !response.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(Class<T> tClass,
            String fieldName,
            String value,
            String sortBy) {

        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .beginGroup()
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable();
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(Class<T> tClass,
            String fieldName,
            boolean value,
            String sortBy) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable();
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFiltered(Class<T> tClass,
            String fieldName,
            boolean value) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllAsync()
                .asObservable()
                .filter(response -> !response.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<T> listSingeRealmObject(
            Class<T> tClass,
            String primaryKeyColumnName, String primaryKey) {
        return mRealm
                .where(tClass)
                .equalTo(primaryKeyColumnName, primaryKey)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
                .map(mRealm::copyFromRealm);
    }

    public Observable<DictionaryEntry> getSingleDictionaryEntry(String key) {
        return mRealm
                .where(DictionaryEntry.class)
                .equalTo("key", key)
                .findFirstAsync()
                .<DictionaryEntry>asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
                .map(mRealm::copyFromRealm);

    }
}

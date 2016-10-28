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
import rx.functions.Func1;
import rx.functions.Func2;

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

    public void saveTransactionalSync(RealmObject object) {
        mRealm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(object);
        });
    }

    public <T extends RealmObject> Observable<T> getObject(Class<T> tClass, String id) {
        return mRealm.where(tClass)
                .equalTo("id", id)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
    }

    @SuppressWarnings("unchecked")
    public <T extends RealmObject> void updateTransactional(Class<T> tClass,
                                                            String id, Func2<T, Realm, Boolean> update) {
        mRealm.executeTransactionAsync(realm -> {
            final T realmObject = realm.where(tClass).equalTo("id", id).findFirst();
            if (realmObject.isLoaded()) {
                update.call(realmObject, realm);
            }
        });
    }

    public void deleteTransactional(final RealmObject realmObject) {
        mRealm.executeTransactionAsync(realm -> {
            realm.copyToRealmOrUpdate(realmObject).deleteFromRealm();
        });
    }

    public void deleteTransactionalSync(final RealmObject realmObject) {
        mRealm.executeTransaction(realm -> {
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

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsExcluded(Class<T>
            tClass,
            String fieldName,
            String value) {
        return mRealm
                .where(tClass)
                .notEqualTo(fieldName, value)
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
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(Class<T> tClass,
                                                                                                String fieldName,
                                                                                                boolean value,
                                                                                                String sortBy) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
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

    public <T extends RealmObject> List<T> restoreIfExist(List<T> objects,
                                                          Class<T> tClass, Func1<T, String> getId, Func2<T, T, Boolean> update) {
        if (objects == null || objects.isEmpty()) {
            return objects;
        }
        for (T object : objects) {
            restore(object, tClass, getId, update);
        }
        return objects;
    }

    private <T extends RealmObject> void restore(T object, Class<T> tClass,
                                                 Func1<T, String> getId, Func2<T, T, Boolean> update) {
        if (object == null) {
            return;
        }

        final T realmObject = mRealm.where(tClass).equalTo("id", getId.call(object)).findFirst();
        if (realmObject == null) {
            return;
        }
        update.call(object, realmObject);
    }
}

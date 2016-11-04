package com.applikey.mattermost.storage.db;

import android.util.Log;

import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.functions.Func2;

public class Db {

    // TODO Use server as realm identifier
    private static final String REALM_NAME = "Test1.realm";
    private static final String TAG = Db.class.getSimpleName();

    private final Realm mRealm;

    public Db(Realm realm) {
        mRealm = realm;
    }

    public void saveTransactional(RealmObject object) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(object));
    }

    public void saveTransactionalSync(RealmObject object) {
        mRealm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(object);
        });
    }

    public <T extends RealmObject> Observable<T> getObject(Class<T> tClass, String id) {
        return getObjectQualified(tClass, "id", id);
    }

    public <T extends RealmObject> Observable<List<T>> getObjectsQualifiedWithCopy(Class<T> tClass,
            String fieldName,
            String[] fieldValue) {
        return mRealm.where(tClass)
                .in(fieldName, fieldValue)
                .findAllAsync()
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid() && !o.isEmpty())
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<T> getObjectQualified(Class<T> tClass,
            String fieldName,
            String fieldValue) {
        return mRealm.where(tClass)
                .equalTo(fieldName, fieldValue)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
    }

    public <T extends RealmObject> Observable<T> getObjectWithCopy(Class<T> tClass, String id) {
        return getObject(tClass, id)
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> T copyFromRealm(T object) {
        return mRealm.copyFromRealm(object);
    }

    @SuppressWarnings("unchecked")
    public <T extends RealmObject> void updateTransactional(Class<T> tClass,
            String id, Func2<T, Realm, Boolean> update) {
        mRealm.executeTransactionAsync(realm -> {
            final T realmObject = realm.where(tClass).equalTo("id", id).findFirst();
            update.call(realmObject, realm);
        });
    }

    public void doTransactional(Func1<Realm, Boolean> update) {
        mRealm.executeTransactionAsync(update::call);
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

    public void saveTransactional(Iterable<? extends RealmObject> objects) {
        mRealm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(objects);
        });
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjects(Class<T> tClass) {
        return mRealm
                .where(tClass)
                .findAllAsync()
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
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

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSortedExcept(Class<T> tClass,
            String fieldName,
            String value,
            String exceptFieldName,
            String exceptValue,
            String sortBy) {

        return mRealm
                .where(tClass)
                .notEqualTo(exceptFieldName, exceptValue)
                .contains(fieldName, value)
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
            restoreIfExist(object, tClass, getId, update);
        }
        return objects;
    }

    public <T extends RealmObject> void restoreIfExist(T object, Class<T> tClass,
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

    void deleteDatabase() {
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();
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
            if (i + 1 < fields.length) {
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

    public <T extends RealmObject> Single<T> getObject(Class<T> tClass, String field, String id) {
        final Realm realm = getRealm();
        T object = realm.where(tClass)
                .contains(field, id)
                .findFirst();
        final Observable<T> map;
        if (object == null) {
            map = Observable.error(new ObjectNotFoundException());
        } else {
            map = object
                    .<T>asObservable()
                    .first()
                    .doOnUnsubscribe(realm::close)
                    .map(realmResult -> {
                        Log.d(TAG, "getObject: " + realmResult);
                        return realmResult;
                    })
                    .map(realm::copyFromRealm);
        }
        return map.toSingle();
    }

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}

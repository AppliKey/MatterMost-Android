package com.applikey.mattermost.storage.db;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import com.annimon.stream.Stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action3;
import rx.functions.Func1;
import rx.functions.Func2;

public class Db {

    // TODO Use server as realm identifier
    private static final String TAG = Db.class.getSimpleName();

    private static final String FIELD_ID = "id";
    private final Realm mRealm;

    public Db(Realm realm) {
        mRealm = realm;
    }

    public void saveTransactional(RealmObject object) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(object));
    }

    public void saveTransactionalSync(RealmObject object) {
        mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(object));
    }

    public <T extends RealmObject> Observable<T> getObject(Class<T> tClass, String id) {
        return getObjectQualified(tClass, FIELD_ID, id);
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

    public <E extends RealmObject> Observable<List<E>> getCopiedObjects(Func1<Realm, RealmResults<E>> resultsHandler) {
        final HandlerThread handlerThread = new HandlerThread("RealmReadThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        final Scheduler scheduler = AndroidSchedulers.from(handlerThread.getLooper());
        final AtomicReference<Realm> realmReference = new AtomicReference<>(null);
        return Observable.defer(() -> {
            final Realm realm = Realm.getDefaultInstance();
            realmReference.set(realm);
            return resultsHandler.call(realm).asObservable();
        })
                .filter(results -> results.isLoaded() && results.isValid())
                .map(results -> realmReference.get().copyFromRealm(results))
                .subscribeOn(scheduler)
                .unsubscribeOn(scheduler)
                .doOnUnsubscribe(() -> unsubscribeOnDbThread(handlerThread, realmReference.get()));
    }

    public <E extends RealmObject> Observable<E> getCopiedObject(Func1<Realm, E> objectHandler) {
        final HandlerThread handlerThread = new HandlerThread("RealmReadThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        final Scheduler scheduler = AndroidSchedulers.from(handlerThread.getLooper());
        final AtomicReference<Realm> realmReference = new AtomicReference<>(null);
        return Observable.defer(() -> {
            final Realm realm = Realm.getDefaultInstance();
            realmReference.set(realm);
            final E object = objectHandler.call(realm);
            return object == null ? Observable.empty() : object.asObservable();
        })
                .filter(realmObject -> realmObject.isLoaded() && realmObject.isValid())
                .map(realmObject -> {
                    //noinspection unchecked
                    return realmReference.get().copyFromRealm((E) realmObject);
                })
                .subscribeOn(scheduler)
                .unsubscribeOn(scheduler)
                .doOnUnsubscribe(() -> unsubscribeOnDbThread(handlerThread, realmReference.get()));
    }

    private void unsubscribeOnDbThread(HandlerThread handlerThread, Realm realm) {
        final Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            realm.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                handlerThread.quitSafely();
            } else {
                handlerThread.quit();
            }
        });
    }

    public <T extends RealmObject> Observable<T> getObjectQualified(Class<T> tClass,
                                                                    String fieldName,
                                                                    String fieldValue) {
        return mRealm.where(tClass)
                .equalTo(fieldName, fieldValue)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded() && o.isValid())
                .first();
    }

    public <T extends RealmObject> Observable<T> getObjectQualifiedNullable(Class<T> tClass,
                                                                    String fieldName,
                                                                    String fieldValue) {
        return mRealm.where(tClass)
                .equalTo(fieldName, fieldValue)
                .findFirstAsync()
                .<T>asObservable()
                .filter(o -> o.isLoaded())
                .first();
    }

    // FIXME Duplicated
    public <T extends RealmObject> Observable<T> getObjectAndCopy(Class<T> tClass, String id) {
        return getObject(tClass, id)
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> Observable<T> getObjectWithCopy(Class<T> tClass, String id) {
        return getObject(tClass, id)
                .map(mRealm::copyFromRealm);
    }

    public <T extends RealmObject> T copyFromRealm(T object) {
        return mRealm.copyFromRealm(object);
    }

    public <T extends RealmObject> void removeAsync(Class tClass, String field, String value) {
        mRealm.executeTransaction(realm -> realm.where(tClass)
                .equalTo(field, value)
                .findAll()
                .deleteAllFromRealm());

    }

    @SuppressWarnings("unchecked")
    public <T extends RealmObject> void updateTransactional(Class<T> tClass,
                                                            String id,
                                                            Func2<T, Realm, Boolean> update) {
        mRealm.executeTransactionAsync(realm -> {
            final T realmObject = realm.where(tClass).equalTo(FIELD_ID, id).findFirst();
            update.call(realmObject, realm);
        });
    }

    public <T extends RealmObject> void updateTransactional(Class<T> tClass,
                                                            String id,
                                                            Func2<T, Realm, Boolean> update,
                                                            Realm.Transaction.OnSuccess onSuccess) {
        mRealm.executeTransactionAsync(realm -> {
            final T realmObject = realm.where(tClass).equalTo(FIELD_ID, id).findFirst();
            update.call(realmObject, realm);
        }, onSuccess);
    }

    public <T extends RealmObject, V> void updateMapTransactional(Map<String, V> valuesMap,
                                                                  Class<T> clazz,
                                                                  Action3<T, V, Realm> updateFunc) {
        mRealm.executeTransactionAsync(realm -> Stream.of(valuesMap.entrySet())
                .forEach(entry -> {
                    final T object = realm.where(clazz).equalTo(FIELD_ID, entry.getKey()).findFirst();
                    updateFunc.call(object, entry.getValue(), realm);
                }));
    }

    public <T extends RealmObject, V> void updateMapTransactionalSync(Map<String, V> valuesMap,
                                                                      Class<T> clazz,
                                                                      Action3<T, V, Realm> updateFunc) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> Stream.of(valuesMap.entrySet())
                .forEach(entry -> {
                    final T object = realm.where(clazz).equalTo("id", entry.getKey()).findFirst();
                    updateFunc.call(object, entry.getValue(), realm);
                }));
        realmInstance.close();
    }

    public void doTransactional(Action1<Realm> update) {
        mRealm.executeTransactionAsync(update::call);
    }

    public void doTransactionalWithCallback(Action1<Realm> action, Realm.Transaction.OnSuccess callback) {
        mRealm.executeTransactionAsync(action::call, callback);
    }

    public <T extends RealmObject> void deleteTransactional(Class<T> tClass, String id) {
        mRealm.executeTransactionAsync(realm -> {
            realm.where(tClass).equalTo(FIELD_ID, id).findFirst().deleteFromRealm();
        });
    }

    public <T extends RealmObject> void deleteTransactionalSync(Class<T> tClass, String id) {
        mRealm.beginTransaction();
        mRealm.where(tClass).equalTo(FIELD_ID, id).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
    }

    public void deleteTransactional(final RealmObject realmObject) {
        mRealm.executeTransactionAsync(realm -> {
            realm.copyToRealmOrUpdate(realmObject).deleteFromRealm();
        });
    }

    public void deleteTransactionalSync(final RealmObject realmObject) {
        mRealm.executeTransaction(realm -> {
            realmObject.deleteFromRealm();
//            realm.copyToRealmOrUpdate(realmObject).deleteFromRealm();
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
        mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(objects));
    }

    public void saveTransactionalSync(Iterable<? extends RealmObject> objects) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(objects);
        mRealm.commitTransaction();
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

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredExcluded(
            Class<T> tClass,
            String fieldName,
            String value,
            String excludedField,
            boolean excludedValue,
            String sortBy) {

        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .equalTo(excludedField, excludedValue)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid() && !o.isEmpty());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredExcludedWithEmpty(
            Class<T> tClass,
            String fieldName,
            boolean value,
            String excludedField,
            boolean excludedValue,
            String sortBy) {

        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .equalTo(excludedField, excludedValue)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(
            Class<T> tClass,
            String fieldName,
            String value,
            String sortBy) {

        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid() && !o.isEmpty());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSortedWithEmpty(
            Class<T> tClass,
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

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSortedWithEmpty(
            Class<T> tClass,
            String fieldName1,
            String value1,
            String fieldName2,
            boolean value2,
            String sortBy) {

        return mRealm
                .where(tClass)
                .equalTo(fieldName1, value1)
                .equalTo(fieldName2, value2)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSortedWithEmpty(
            Class<T> tClass,
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

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(
            Class<T> tClass,
            String fieldName,
            String[] value,
            String sortBy) {

        return mRealm
                .where(tClass)
                .in(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid() && !o.isEmpty());
    }

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSortedExcluded(
            Class<T> tClass,
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

    public <T extends RealmObject> Observable<RealmResults<T>> resultRealmObjectsFilteredSorted(
            Class<T> tClass,
            String fieldName,
            boolean value,
            String sortBy) {
        return mRealm
                .where(tClass)
                .equalTo(fieldName, value)
                .findAllSortedAsync(sortBy, Sort.DESCENDING)
                .asObservable()
                .filter(o -> o.isLoaded() && o.isValid() && !o.isEmpty());
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
                                                          Class<T> tClass,
                                                          Func1<T, String> getId,
                                                          Func2<T, T, Boolean> update) {
        if (objects == null || objects.isEmpty()) {
            return objects;
        }
        for (T object : objects) {
            restoreIfExist(object, tClass, getId, update);
        }
        return objects;
    }

    public <T extends RealmObject> void restoreIfExist(T object,
                                                       Class<T> tClass,
                                                       Func1<T, String> getId,
                                                       Func2<T, T, Boolean> update) {
        if (object == null) {
            return;
        }

        final T realmObject = mRealm.where(tClass).equalTo(FIELD_ID, getId.call(object)).findFirst();
        if (realmObject == null) {
            return;
        }
        update.call(object, realmObject);
    }

    public <T extends RealmObject> Observable<List<T>> listRealmObjectsFilteredSorted(Class<T>
                                                                                              tClass,
                                                                                      String text,
                                                                                      String[] fields,
                                                                                      String sortedField) {
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

        return query.findAllSorted(sortedField)
                .asObservable()
                .map(realm::copyFromRealm);
    }

    public <T extends RealmObject> Single<T> getObject(Class<T> tClass, String field, String id) {
        final Realm realm = getRealm();
        final T object = realm.where(tClass)
                .contains(field, id)
                .findFirst();
        final Observable<T> map;
        if (object == null) {
            map = Observable.error(new ObjectNotFoundException("Field : " + field + " = " + id));
        } else {
            map = object
                    .<T>asObservable()
                    .first()
                    .map(realmResult -> {
                        Log.d(TAG, "getObject: " + realmResult);
                        return realmResult;
                    })
                    .map(realm::copyFromRealm);
        }
        return map.toSingle();
    }

    void deleteDatabase() {
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();
    }

    public Realm getRealm() {
        return mRealm;
    }

}

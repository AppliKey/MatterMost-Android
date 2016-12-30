package com.applikey.mattermost.storage.db;

import android.text.TextUtils;
import android.util.Log;

import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Single;

public class PostStorage {

    private final Db mDb;

    public PostStorage(Db db) {
        mDb = db;
    }

    public void delete(Post post) {
        mDb.deleteTransactionalSync(post);
    }

    public void update(Post post) {
        mDb.saveTransactional(post);
    }

    public Single<Post> get(String id) {
        return mDb.getObject(Post.class, Post.FIELD_NAME_ID, id);
    }

    public void saveAll(List<Post> posts) {
        mDb.doTransactional(realm -> {
            for (Post post : posts) {
                final User author = realm.where(User.class)
                        .equalTo(User.FIELD_NAME_ID, post.getUserId())
                        .findFirst();

                final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                        realm.where(Post.class)
                                .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                                .findFirst()
                        : null;

                final Post realmPost = realm.copyToRealmOrUpdate(post);
                realmPost.setAuthor(author);
                realmPost.setRootPost(rootPost);
            }
        });
    }

    public void saveAllSync(List<Post> posts) {
        Realm realm = mDb.getRealm();
        realm.beginTransaction();
            for (Post post : posts) {
                final User author = realm.where(User.class)
                        .equalTo(User.FIELD_NAME_ID, post.getUserId())
                        .findFirst();

                final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                        realm.where(Post.class)
                                .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                                .findFirst()
                        : null;

                final Post realmPost = realm.copyToRealmOrUpdate(post);
                realmPost.setAuthor(author);
                realmPost.setRootPost(rootPost);
            }
       realm.commitTransaction();
    }

    public void save(Post post) {
        mDb.doTransactional(realm -> {
            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, post.getUserId())
                    .findFirst();

            final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                    realm.where(Post.class)
                            .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                            .findFirst()
                    : null;

            final Post realmPost = realm.copyToRealmOrUpdate(post);
            realmPost.setAuthor(author);
            realmPost.setRootPost(rootPost);
        });
    }

    public Post copyFromDb(Post post) {
        return mDb.copyFromRealm(post);
    }

    public Observable<RealmResults<Post>> listByChannel(String channelId) {
        return mDb.resultRealmObjectsFilteredSortedWithEmpty(Post.class, Post.FIELD_NAME_CHANNEL_ID,
                channelId, Post.FIELD_NAME_CHANNEL_CREATE_AT);
    }

    public void deleteAllByChannel(String channelId, boolean excludeFailed) {
        Log.d("PostStorage ", "deleteAllByChannel: ");
        mDb.doTransactional(realm -> realm.where(Post.class)
                .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                .equalTo(Post.FIELD_NAME_SENT, excludeFailed)
                .findAll()
                .deleteAllFromRealm());
    }

    public void delete(String id) {
        mDb.deleteTransactionalSync(Post.class, id);
    }
}

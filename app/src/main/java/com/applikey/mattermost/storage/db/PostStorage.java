package com.applikey.mattermost.storage.db;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;

import java.util.List;

import com.applikey.mattermost.utils.Callback;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
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
        if (post.getAuthor() == null) {
            throw new RuntimeException("Trying to save post with null author");
        }

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

                if (author == null) {
                    throw new RuntimeException("Trying to save post with null author");
                }

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

    public void deleteSync(Post post) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> {
            final Post removalPost = realm.where(Post.class).equalTo(Post.FIELD_NAME_ID, post.getId()).findFirst();
            if (removalPost == null) {
                return;
            }

            removalPost.deleteFromRealm();

            final Post lastPost = realm.where(Post.class)
                    .equalTo(Post.FIELD_NAME_CHANNEL_ID, post.getChannelId())
                    .findAllSorted(Post.FIELD_NAME_CHANNEL_CREATE_AT, Sort.DESCENDING)
                    .first();

            if (lastPost == null) {
                return;
            }

            final Channel channel = realm.where(Channel.class)
                    .equalTo(Channel.FIELD_ID, post.getChannelId())
                    .findFirst();

            if (channel == null) {
                return;
            }

            channel.setLastPost(lastPost);
        });
        realmInstance.close();
    }

    public void saveSync(Post post) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> realm.copyToRealmOrUpdate(post));
        realmInstance.close();
    }

    public void saveAll(List<Post> posts, Callback callback) {
        mDb.doTransactionalWithCallback(realm -> {
            for (Post post : posts) {
                final User author = realm.where(User.class)
                        .equalTo(User.FIELD_NAME_ID, post.getUserId())
                        .findFirst();

                if (author == null) {
                    throw new RuntimeException("Trying to save post with null author");
                }

                final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                        realm.where(Post.class)
                                .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                                .findFirst()
                        : null;

                final Post realmPost = realm.copyToRealmOrUpdate(post);
                realmPost.setAuthor(author);
                realmPost.setRootPost(rootPost);
            }
        }, callback::execute);
    }

    public void saveAllWithClear(List<Post> posts, String channelId, Callback callback) {
        mDb.doTransactionalWithCallback(realm -> {
            realm.where(Post.class)
                    .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                    .findAll()
                    .deleteAllFromRealm();

            for (Post post : posts) {
                final User author = realm.where(User.class)
                        .equalTo(User.FIELD_NAME_ID, post.getUserId())
                        .findFirst();

                if (author == null) {
                    throw new RuntimeException("Trying to save post with null author");
                }

                final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                        realm.where(Post.class)
                                .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                                .findFirst()
                        : null;

                final Post realmPost = realm.copyToRealmOrUpdate(post);
                realmPost.setAuthor(author);
                realmPost.setRootPost(rootPost);
            }
        }, callback::execute);
    }

    public void saveAllSync(List<Post> posts) {
        Realm realm = mDb.getRealm();
        realm.beginTransaction();
        for (Post post : posts) {
            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, post.getUserId())
                    .findFirst();

            if (author == null) {
                throw new RuntimeException("Trying to save post with null author");
            }

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

            if (author == null) {
                throw new RuntimeException("Trying to save post with null author");
            }

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

    public void save(Post post, Callback callback) {
        mDb.doTransactionalWithCallback(realm -> {
            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, post.getUserId())
                    .findFirst();

            if (author == null) {
                throw new RuntimeException("Trying to save post with null author");
            }

            final Post rootPost = !TextUtils.isEmpty(post.getRootId()) ?
                    realm.where(Post.class)
                            .equalTo(Post.FIELD_NAME_ID, post.getRootId())
                            .findFirst()
                    : null;

            final Post realmPost = realm.copyToRealmOrUpdate(post);
            realmPost.setAuthor(author);
            realmPost.setRootPost(rootPost);
        }, callback::execute);
    }

    public Post copyFromDb(Post post) {
        return mDb.copyFromRealm(post);
    }

    public Observable<RealmResults<Post>> listByChannel(String channelId) {
        return mDb.resultRealmObjectsFilteredSortedWithEmpty(Post.class, Post.FIELD_NAME_CHANNEL_ID,
                channelId, Post.FIELD_NAME_CHANNEL_CREATE_AT);
    }

    public void deleteAllByChannel(String channelId, boolean excludeFailed) {
        mDb.doTransactional(realm -> realm.where(Post.class)
                .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                .equalTo(Post.FIELD_NAME_SENT, excludeFailed)
                .findAll()
                .deleteAllFromRealm());
    }

    public void deleteAllByChannel(String channelId, Realm.Transaction.OnSuccess callback) {
        mDb.doTransactionalWithCallback(realm -> realm.where(Post.class)
                .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                .findAll()
                .deleteAllFromRealm(), callback);
    }

    public void delete(String id) {
        mDb.deleteTransactionalSync(Post.class, id);
    }

    public void loadAuthor(Post post) {
        final Realm realmInstance = Realm.getDefaultInstance();

        realmInstance.executeTransaction(realm -> {
            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, post.getUserId())
                    .findFirst();

            post.setAuthor(author);
        });
    }
}

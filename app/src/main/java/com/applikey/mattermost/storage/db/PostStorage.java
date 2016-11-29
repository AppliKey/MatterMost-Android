package com.applikey.mattermost.storage.db;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

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
        return mDb.resultRealmObjectsFilteredSorted(Post.class, Post.FIELD_NAME_CHANNEL_ID,
                                                    channelId, Post.FIELD_NAME_CHANNEL_CREATE_AT);
    }
}

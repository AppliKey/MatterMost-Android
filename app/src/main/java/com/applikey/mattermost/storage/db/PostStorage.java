package com.applikey.mattermost.storage.db;

import com.annimon.stream.Stream;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class PostStorage { //TODO replace all realm things to Db

    private final Db mDb;

    public PostStorage(Db db) {
        mDb = db;
    }

    public void saveAllWithRemoval(List<Post> posts) {
        Realm realm = Realm.getDefaultInstance();
        Stream.of(posts).forEach(post -> post.setUser(realm.where(User.class)
                .equalTo("id", post.getUserId())
                .findFirst()));
        realm.close();
        mDb.saveTransactional(posts);
    }

    public void saveAsync(Post post) {
        mDb.saveTransactional(post);
    }

    public void delete(Post post) {
        mDb.deleteTransactional(post);
    }


    public void update(Post post) { //TODO ADD logic to autoupdate
        mDb.saveTransactional(post);
    }

    public void saveAll(List<Post> posts) {
        Realm realm = Realm.getDefaultInstance();
        Stream.of(posts).forEach(post -> post.setUser(realm.where(User.class)
                .equalTo("id", post.getUserId())
                .findFirst()));
        realm.close();
        mDb.saveTransactional(posts);
    }

    public Observable<RealmResults<Post>> listByChannel(String channelId) {
        return mDb.listRealmObjectsFilteredSortedAsync(Post.class, Post.FIELD_NAME_CHANNEL_ID,
                Post.FIELD_NAME_CHANNEL_CREATE_AT, channelId);
    }
}

package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.post.Post;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class PostStorage {

    @Inject
    Db mDb;

    public PostStorage() {
        App.getComponent().inject(this);
    }

    public void saveAll(List<Post> posts) {
        mDb.saveTransactionalWithRemoval(posts);
    }

    public Observable<List<Post>> listByChannel(String channelId) {
        return mDb.listRealmObjectsFilteredSorted(Post.class, Post.FIELD_NAME_CHANNEL_ID,
                Post.FIELD_NAME_CHANNEL_CREATE_AT, channelId);
    }
}

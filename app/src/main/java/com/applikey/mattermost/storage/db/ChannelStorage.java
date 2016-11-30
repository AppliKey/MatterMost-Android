package com.applikey.mattermost.storage.db;

import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.post.LastPostDto;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.functions.Func0;

public class ChannelStorage {

    private static final String TAG = "ChannelStorage";

    private final Db mDb;

    private final Prefs mPrefs;
    private final Scheduler mDbScheduler;

    public ChannelStorage(final Db db, final Prefs prefs, Scheduler dbScheduler) {
        mDb = db;
        mPrefs = prefs;
        mDbScheduler = dbScheduler;
    }

    public Observable<Channel> findById(String id) {
        return mDb.getObject(Channel.class, id);
    }

    public Observable<Channel> get(String id) {
        final AtomicReference<Realm> realmReference = new AtomicReference<>(null);
        return Observable.defer(new Func0<Observable<Channel>>() {
            @Override
            public Observable<Channel> call() {
                final Realm realm = Realm.getDefaultInstance();
                realmReference.set(realm);
                return realm.where(Channel.class).equalTo("id", id).findFirst().asObservable();
            }
        })
                .subscribeOn(mDbScheduler)
                .unsubscribeOn(mDbScheduler)
                .filter(channel -> channel.isLoaded() && channel.isValid())
                .map(channel -> realmReference.get().copyFromRealm(channel))
                .doOnUnsubscribe(() -> realmReference.get().close());
    }

    public void updateLastPost(String channelId, Post post) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> {
            final Post persistedPost = realm.copyToRealmOrUpdate(post);
            final Channel channel = realm.where(Channel.class).equalTo("id", channelId).findFirst();
            if (channel == null) {
                return;
            }
            channel.setLastPost(persistedPost);
        });
        realmInstance.close();
    }

    public void updateLastPost(String channelId) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> {
            final Post lastPost = realm.where(Post.class)
                    .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                    .findAllSorted(Post.FIELD_NAME_CHANNEL_CREATE_AT, Sort.DESCENDING)
                    .first();

            if (lastPost == null) {
                return;
            }

            final Channel channel = realm.where(Channel.class)
                    .equalTo(Channel.FIELD_ID, channelId)
                    .findFirst();

            if (channel == null) {
                return;
            }

            channel.setLastPost(lastPost);

        });
        realmInstance.close();
    }

    public void updateViewedAt(String channelId) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransaction(realm -> {
            final Channel channel = realm.where(Channel.class).equalTo("id", channelId).findFirst();
            if (channel == null) {
                return;
            }
            channel.setHasUnreadMessages(false);
            channel.setLastViewedAt(channel.getLastActivityTime());
        });
        realmInstance.close();
    }

    // TODO Duplicate
    public Observable<Channel> findByIdAndCopy(String id) {
        return mDb.getObjectAndCopy(Channel.class, id);
    }

    public Observable<Channel> directChannel(String userId) {
        return mDb.getObjectQualified(Channel.class, Channel.FIELD_NAME_COLLOCUTOR_ID, userId);
    }

    public void removeChannelAsync(Channel channel) {
        mDb.removeAsync(Channel.class, Channel.FIELD_ID, channel.getId());
    }

    public Observable<List<Channel>> list() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<RealmResults<Channel>> listUndirected(String name) {
        return mDb.resultRealmObjectsFilteredSortedExcluded(Channel.class, Channel.FIELD_NAME,
                                                            name,
                                                            Channel.FIELD_NAME_TYPE,
                                                            Channel.ChannelType.DIRECT
                                                                    .getRepresentation(),
                                                            Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listOpen() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                                                    Channel.ChannelType.PUBLIC.getRepresentation(),
                                                    Channel.FIELD_NAME_LAST_ACTIVITY_TIME)
                .first();
    }

    public Observable<RealmResults<Channel>> listClosed() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                                                    Channel.ChannelType.PRIVATE.getRepresentation(),
                                                    Channel.FIELD_NAME_LAST_ACTIVITY_TIME)
                .first();
    }

    public Observable<RealmResults<Channel>> listDirect() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                                                    Channel.ChannelType.DIRECT.getRepresentation(),
                                                    Channel.FIELD_NAME_LAST_ACTIVITY_TIME)
                .first();
    }

    public Observable<RealmResults<Channel>> listFavorite() {
        return mDb.resultRealmObjectsFilteredSortedWithEmpty(Channel.class,
                                                             Channel.IS_FAVORITE, true,
                                                             Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<Channel> channelById(String id) {
        return mDb.getObject(Channel.class, id);
    }

    public Observable<List<Channel>> listAll() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<RealmResults<Channel>> listUnread() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_UNREAD_TYPE,
                                                    true,
                                                    Channel.FIELD_NAME_LAST_ACTIVITY_TIME)
                .first();
    }

    public void save(Channel channel) {
        mDb.saveTransactional(channel);
    }

    public void save(List<Channel> channels) {
        mDb.saveTransactional(channels);
    }

    public Observable<List<Membership>> listMembership() {
        return mDb.listRealmObjects(Membership.class);
    }

    public void updateLastPost(Channel channel) {
        final Post lastPost = channel.getLastPost();
        setLastPost(channel, lastPost);
    }

    public void setFavorite(Channel channel, boolean isFavorite) {
        mDb.updateTransactional(Channel.class, channel.getId(), (channel1, realm) -> {
            channel1.setFavorite(isFavorite);
            return true;
        });
    }

    public void setLastPost(Channel channel, Post lastPost) {
        final String channelId = channel.getId();
        mDb.updateTransactional(Channel.class, channel.getId(), (realmChannel, realm) -> {
            final Post realmPost;

            //If last post null, find last post
            if (lastPost == null) {
                final RealmResults<Post> result = realm.where(Post.class)
                        .equalTo(Post.FIELD_NAME_CHANNEL_ID, channelId)
                        .findAllSorted(Post.FIELD_NAME_CHANNEL_CREATE_AT, Sort.DESCENDING);
                if (result.size() > 0) {
                    realmPost = result.first();
                } else {
                    return false;
                }
            } else {
                realmPost = realm.copyToRealmOrUpdate(lastPost);
            }
            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, realmPost.getUserId())
                    .findFirst();

            final Post rootPost = !TextUtils.isEmpty(realmPost.getRootId()) ?
                    realm.where(Post.class)
                            .equalTo(Post.FIELD_NAME_ID, realmPost.getRootId())
                            .findFirst()
                    : null;

            realmPost.setAuthor(author);
            realmPost.setRootPost(rootPost);
            realmChannel.setLastPost(realmPost);
            realmChannel.updateLastActivityTime();
            return true;
        });
    }

    public void updateLastPosts(List<LastPostDto> lastPosts) {
        final String currentUserId = mPrefs.getCurrentUserId();
        mDb.doTransactional(realm -> {
            final User currentUser = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, currentUserId)
                    .findFirst();
            for (int i = 0; i < lastPosts.size(); i++) {
                if (lastPosts.get(i) == null) {
                    continue;
                }
                final String channelId = lastPosts.get(i).getChannelId();

                final Post realmPost = realm.copyToRealmOrUpdate(lastPosts.get(i).getPost());

                final Channel realmChannel = realm.where(Channel.class)
                        .equalTo(Channel.FIELD_ID, channelId)
                        .findFirst();

                final User author;
                if (TextUtils.equals(realmPost.getUserId(), currentUserId)) {
                    author = currentUser;
                } else {
                    author = realm.where(User.class)
                            .equalTo(User.FIELD_NAME_ID, realmPost.getUserId())
                            .findFirst();
                }

                final Post rootPost = !TextUtils.isEmpty(realmPost.getRootId()) ?
                        realm.where(Post.class)
                                .equalTo(Post.FIELD_NAME_ID, realmPost.getRootId())
                                .findFirst()
                        : null;

                realmPost.setAuthor(author);
                realmPost.setRootPost(rootPost);
                realmChannel.setLastPost(realmPost);
                realmChannel.updateLastActivityTime();
            }
        });
    }

    public void updateLastViewedAt(String id) {
        mDb.updateTransactional(Channel.class, id, (realmChannel, realm) -> {
            if (realmChannel != null) {
                realmChannel.setHasUnreadMessages(false);
                realmChannel.setLastViewedAt(realmChannel.getLastActivityTime());
            }
            return true;
        });
    }

    public void setLastViewedAt(String id, long lastViewAt) {
        mDb.updateTransactional(Channel.class, id, (realmChannel, realm) -> {
            realmChannel.setHasUnreadMessages(false);
            realmChannel.setLastViewedAt(lastViewAt);
            return true;
        });
    }

    public void setUsers(String id, List<User> users) {
        mDb.updateTransactional(Channel.class, id, (realmChannel, realm) -> {
            realmChannel.setUsers(users);
            realm.copyToRealmOrUpdate(realmChannel);
            return true;
        });
    }

    public void saveChannelResponse(ChannelResponse response, Map<String, User> userProfiles) {
        // Transform direct channels

        final String currentUserId = mPrefs.getCurrentUserId();
        final String directChannelType = Channel.ChannelType.DIRECT.getRepresentation();

        final Map<String, Membership> membership = response.getMembershipEntries();

        final List<Channel> channels = response.getChannels();

        Stream.of(channels).forEach(channel -> {
            channel.updateLastActivityTime();
            if (channel.getType().equals(directChannelType)) {
                updateDirectChannelData(channel, userProfiles, currentUserId);
            }

            final Membership membershipData = membership.get(channel.getId());
            if (membershipData != null) {
                channel.setLastViewedAt(membershipData.getLastViewedAt());
            }
        });

        saveAndDeleteRemovedChannels(channels);
    }

    private void saveAndDeleteRemovedChannels(List<Channel> channels) {
        mDb.saveTransactional(restoreChannels(channels));
        mDb.doTransactional(realm -> {
            final String[] ids = Stream.of(channels).map(Channel::getId).toArray(String[]::new);
            if (ids.length == 0) {
                return;
            }
            realm.where(Channel.class).not().in(Channel.FIELD_ID, ids).findAll().deleteAllFromRealm();
        });
    }

    public Single<Channel> getChannel(String id) {
        return mDb.getObject(Channel.class, Channel.FIELD_NAME, id);
    }

    public void delete(String channelId) {
        mDb.deleteTransactional(Channel.class, channelId);
    }

    private List<Channel> restoreChannels(List<Channel> channels) {
        return mDb.restoreIfExist(channels, Channel.class, Channel::getId,
                                  (channel, storedChannel) -> {
                                      channel.setUsers(storedChannel.getUsers());
                                      channel.setLastPost(storedChannel.getLastPost());
                                      channel.updateLastActivityTime();
                                      channel.setFavorite(storedChannel.isFavorite());
                                      return true;
                                  });
    }

    public void updateDirectChannelData(Channel channel,
                                        Map<String, User> contacts,
                                        String currentUserId) {
        final String channelName = channel.getName();
        final String otherUserId = extractOtherUserId(channelName, currentUserId);

        final User user = contacts.get(otherUserId);
        if (user != null) {
            channel.setDirectCollocutor(user);
            channel.setDisplayName(User.getDisplayableName(user));
        }
    }

    private String extractOtherUserId(String channelName, String currentUserId) {
        return channelName.replace(currentUserId, "").replace("__", "");
    }
}

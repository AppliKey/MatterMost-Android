package com.applikey.mattermost.storage.db;

import android.support.annotation.NonNull;
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

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Single;

public class ChannelStorage {

    private static final String TAG = "ChannelStorage";

    private final Db mDb;

    private final Prefs mPrefs;

    public ChannelStorage(final Db db, final Prefs prefs) {
        mDb = db;
        mPrefs = prefs;
    }

    public Observable<Channel> findById(String id) {
        return mDb.getObject(Channel.class, id);
    }

    public Observable<Channel> get(String id) {
        return mDb.getCopiedObject(realm -> realm.where(Channel.class).equalTo("id", id).findFirst());
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
            channel.setLastActivityTime(persistedPost.getCreatedAt());
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
        return mDb.resultRealmObjectsFilteredExcluded(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PUBLIC.getRepresentation(),
                Channel.FIELD_NAME_IS_JOINED,
                true,
                Channel.FIELD_NAME_HAS_UNREAD,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listClosed() {
        return mDb.resultRealmObjectsFilteredExcluded(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PRIVATE.getRepresentation(),
                Channel.FIELD_NAME_IS_JOINED,
                true,
                Channel.FIELD_NAME_HAS_UNREAD,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listDirect() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.DIRECT.getRepresentation(),
                Channel.FIELD_NAME_HAS_UNREAD,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listFavorite() {
        return mDb.resultRealmObjectsFilteredSortedWithEmpty(Channel.class,
                Channel.IS_FAVORITE, true,
                Channel.FIELD_NAME_HAS_UNREAD,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listUnread() {
        return mDb.resultRealmObjectsFilteredExcludedWithEmpty(Channel.class, Channel.FIELD_UNREAD_TYPE,
                true,
                Channel.FIELD_NAME_IS_JOINED,
                true,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<Channel> channelById(String id) {
        return mDb.getObject(Channel.class, id);
    }

    public Observable<List<Channel>> listAll() {
        return mDb.listRealmObjects(Channel.class);
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

    public void setFavorite(String channelId, boolean isFavorite) {
        mDb.updateTransactional(Channel.class, channelId, (channel, realm) -> {
            channel.setFavorite(isFavorite);
            return true;
        });
    }

    public void setLastPost(@NonNull Channel channel, Post lastPost) {
        mDb.updateTransactional(Channel.class, channel.getId(), (realmChannel, realm) ->
                performSetLastPost(realm, realmChannel, lastPost));
    }

    public void setLastPostSync(@NonNull Channel channel, Post lastPost) {
        mDb.updateTransactionalSync(Channel.class, channel.getId(), (realmChannel, realm) ->
                performSetLastPost(realm, realmChannel, lastPost));
    }

    private boolean performSetLastPost(Realm realm, Channel channel, Post lastPost) {
        Post realmPost = null;
        if (lastPost != null) {
            realmPost = realm.copyToRealmOrUpdate(lastPost);

            final User author = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, realmPost.getUserId())
                    .findFirst();

            final Post rootPost = !TextUtils.isEmpty(realmPost.getRootId()) ?
                    realm.where(Post.class)
                            .equalTo(Post.FIELD_NAME_ID, realmPost.getRootId())
                            .findFirst() : null;

            realmPost.setAuthor(author);
            realmPost.setRootPost(rootPost);
        }
        channel.setLastPost(realmPost);
        channel.updateLastActivityTime();
        if (realmPost != null && TextUtils.equals(realmPost.getAuthor().getId(), mPrefs.getCurrentUserId())) {
            channel.setHasUnreadMessages(false);
        }
        return true;
    }

    public void updateLastPosts(LastPostDto lastPostDto) {
        final String currentUserId = mPrefs.getCurrentUserId();

        final Post lastPost = lastPostDto.getPost();

        mDb.doTransactional(realm -> {
            final User currentUser = realm.where(User.class)
                    .equalTo(User.FIELD_NAME_ID, currentUserId)
                    .findFirst();

            final String channelId = lastPost.getChannelId();

            final Post realmPost = realm.copyToRealmOrUpdate(lastPost);

            final Channel realmChannel = realm.where(Channel.class)
                    .equalTo(Channel.FIELD_ID, channelId)
                    .findFirst();

            final User author;
            if (TextUtils.equals(lastPost.getUserId(), currentUserId)) {
                author = currentUser;
                realmChannel.setHasUnreadMessages(false);
            } else {
                author = realm.where(User.class)
                        .equalTo(User.FIELD_NAME_ID, lastPost.getUserId())
                        .findFirst();
            }

            final Post rootPost = !TextUtils.isEmpty(lastPost.getRootId()) ?
                    realm.where(Post.class)
                            .equalTo(Post.FIELD_NAME_ID, lastPost.getRootId())
                            .findFirst()
                    : null;

            realmPost.setAuthor(author);
            realmPost.setRootPost(rootPost);

            realmChannel.setLastPost(realmPost);
            realmChannel.updateLastActivityTime();
            if (TextUtils.equals(author.getId(), mPrefs.getCurrentUserId())) {
                realmChannel.setHasUnreadMessages(false);
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
        mDb.updateTransactionalSync(Channel.class, id, (realmChannel, realm) -> {
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

    public void setUsers(String id, List<User> users, Realm.Transaction.OnSuccess onSuccess) {
        mDb.updateTransactional(Channel.class, id, (realmChannel, realm) -> {
            realmChannel.setUsers(users);
            realm.copyToRealmOrUpdate(realmChannel);
            return true;
        }, onSuccess);
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

    public void saveAndDeleteRemovedChannelsSync(List<Channel> channels) {
        mDb.saveTransactional(restoreChannels(channels));
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
        return mDb.getObject(Channel.class, Channel.FIELD_ID, id);
    }

    public Single<Channel> getDirectChannelByCollocutorId(String userId) {
        return mDb.getObject(Channel.class, Channel.FIELD_NAME_COLLOCUTOR_ID, userId);
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
                    channel.setJoined(storedChannel.isJoined());
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

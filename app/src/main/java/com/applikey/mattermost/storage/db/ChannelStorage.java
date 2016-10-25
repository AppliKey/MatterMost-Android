package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.List;
import java.util.Map;

import io.realm.RealmResults;
import rx.Observable;

public class ChannelStorage {

    private static final String TAG = "ChannelStorage";

    private final Db mDb;
    private final Prefs mPrefs;

    public ChannelStorage(final Db db, final Prefs prefs) {
        mDb = db;
        mPrefs = prefs;
    }

    public Observable<List<Channel>> list() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<RealmResults<Channel>> listOpen() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PUBLIC.getRepresentation(),
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listClosed() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PRIVATE.getRepresentation(),
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<RealmResults<Channel>> listDirect() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.DIRECT.getRepresentation(),
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);
    }

    public Observable<List<Channel>> listAll() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<RealmResults<Channel>> listUnread() {
        return mDb.resultRealmObjectsFilteredSorted(Channel.class, Channel.FIELD_UNREAD_TYPE,
                true,
                Channel.FIELD_NAME_LAST_ACTIVITY_TIME);

    }

    public Observable<List<Membership>> listMembership() {
        return mDb.listRealmObjects(Membership.class);
    }

    public void updateChannelData(Channel channel) {
        mDb.updateTransactional(Channel.class, channel.getId(), (realmChannel, realm) -> {
            final Post realmPost = realm.copyToRealmOrUpdate(channel.getLastPost());
            realmChannel.setLastPost(realmPost);
            realmChannel.updateLastActivityTime();
            realmChannel.setLastPostAuthorDisplayName(channel.getLastPostAuthorDisplayName());
            return true;
        });
    }

    public void saveChannelResponse(ChannelResponse response, Map<String, User> userProfiles) {
        // Transform direct channels

        final String currentUserId = mPrefs.getCurrentUserId();
        final String directChannelType = Channel.ChannelType.DIRECT.getRepresentation();

        final Map<String, Membership> membership = response.getMembershipEntries();

        final List<Channel> channels = response.getChannels();
        for (Channel channel : channels) {

            if (channel.getType().equals(directChannelType)) {
                updateDirectChannelData(channel, userProfiles, currentUserId);
            }

            final Membership membershipData = membership.get(channel.getId());
            if (membershipData != null) {
                channel.setLastViewedAt(membershipData.getLastViewedAt());
            }
        }

        mDb.saveTransactional(restoreChannels(channels));
    }

    private List<Channel> restoreChannels(List<Channel> channels) {
        return mDb.restoreIfExist(channels, Channel.class, Channel::getId, (channel, storedChannel) -> {
            channel.setLastPost(storedChannel.getLastPost());
            channel.updateLastActivityTime();
            return true;
        });
    }

    private void updateDirectChannelData(Channel channel,
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

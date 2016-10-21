package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import rx.Observable;

public class ChannelStorage {

    private final Db mDb;
    private final Prefs mPrefs;

    public ChannelStorage(final Db db, final Prefs prefs) {
        mDb = db;
        mPrefs = prefs;
    }

    public Observable<List<Channel>> list() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<List<Channel>> listOpen() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PUBLIC.getRepresentation());
    }

    public Observable<List<Channel>> listClosed() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PRIVATE.getRepresentation());
    }

    public Observable<List<Channel>> listDirect() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.DIRECT.getRepresentation());
    }

    public Observable<List<Channel>> listAll() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<List<Channel>> listUnread() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_UNREAD_TYPE, true);
    }

    public Observable<List<Membership>> listMembership() {
        return mDb.listRealmObjects(Membership.class);
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

        mDb.saveTransactionalWithRemoval(channels);
    }

    private void updateDirectChannelData(Channel channel,
                                         Map<String, User> contacts,
                                         String currentUserId) {
        final String channelId = channel.getName();
        final String otherUserId = extractOtherUserId(channelId, currentUserId);

        final User user = contacts.get(otherUserId);
        if (user != null) {
            channel.setDisplayName(User.getDisplayableName(user));
            channel.setPreviewImagePath(user.getProfileImage());
            channel.setStatus(user.getStatus());
        }
    }

    private String extractOtherUserId(String channelId, String currentUserId) {
        return channelId.replace(currentUserId, "").replace("__", "");
    }
}

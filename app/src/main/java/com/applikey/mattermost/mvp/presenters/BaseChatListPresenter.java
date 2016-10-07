package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelWithMetadata;
import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.user.UserStatus;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public abstract class BaseChatListPresenter extends SingleViewPresenter<ChatListView>
        implements ChatListPresenter {

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    /* package */ BaseChatListPresenter() {
        App.getComponent().inject(this);
    }

    /* package */ ChannelsWithMetadata transform(List<Channel> channels,
                                                 List<Membership> memberships) {
        final ChannelsWithMetadata channelsWithMetadata =
                new ChannelsWithMetadata(channels.size());
        for (Channel channel : channels) {
            channelsWithMetadata.put(channel.getId(), new ChannelWithMetadata(channel));
        }
        for (Membership membership : memberships) {
            final ChannelWithMetadata membershipTuple =
                    channelsWithMetadata.get(membership.getChannelId());
            if (membershipTuple != null) {
                membershipTuple.setMembership(membership);
            }
        }
        return channelsWithMetadata;
    }

    // TODO Tech Debt
    /* package */ ChannelsWithMetadata transform(List<Channel> channels,
                                                 List<Membership> memberships,
                                                 List<User> directContacts,
                                                 List<UserStatus> userStatuses,
                                                 boolean skipRead) {
        final Map<String, User> userMap = new HashMap<>();
        for (User directContact : directContacts) {
            userMap.put(directContact.getId(), directContact);
        }

        final Map<String, UserStatus> statusMap = new HashMap<>();
        for (UserStatus status : userStatuses) {
            statusMap.put(status.getId(), status);
        }

        final String currentUserId = mPrefs.getCurrentUserId();
        final String directChannelType = Channel.ChannelType.DIRECT.getRepresentation();

        final ChannelsWithMetadata channelsWithMetadata =
                new ChannelsWithMetadata(channels.size());
        for (Channel channel : channels) {
            if (channel.getType().equals(directChannelType)) {
                updateDirectChannelData(channel, userMap, currentUserId, statusMap);
            }

            final ChannelWithMetadata channelWithMetadata = new ChannelWithMetadata(channel);
            channelsWithMetadata.put(channel.getId(), channelWithMetadata);
        }
        for (Membership membership : memberships) {
            final ChannelWithMetadata membershipTuple =
                    channelsWithMetadata.get(membership.getChannelId());
            if (membershipTuple != null) {
                membershipTuple.setMembership(membership);
            }
        }

        if (skipRead) {
            final Set<String> keys = new HashSet<>();
            keys.addAll(channelsWithMetadata.keySet());

            for (String key : keys) {
                final ChannelWithMetadata channel = channelsWithMetadata.get(key);
                if (!channel.checkIsUnread()) {
                    channelsWithMetadata.remove(key);
                }
            }
        }

        return channelsWithMetadata;
    }

    private void updateDirectChannelData(Channel channel,
                                         Map<String, User> contacts,
                                         String currentUserId,
                                         Map<String, UserStatus> userStatuses) {
        final String channelId = channel.getName();
        final String otherUserId = extractOtherUserId(channelId, currentUserId);

        final User user = contacts.get(otherUserId);
        if (user != null) {
            channel.setDisplayName(User.getDisplayableName(user));
            channel.setPreviewImagePath(user.getProfileImage());
        }

        final UserStatus status = userStatuses.get(otherUserId);
        if (status != null) {
            channel.setStatus(status.getStatusValue().ordinal());
        }
    }

    private String extractOtherUserId(String channelId, String currentUserId) {
        return channelId.replace(currentUserId, "").replace("__", "");
    }
}


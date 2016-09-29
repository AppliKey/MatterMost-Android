package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelWithMetadata;
import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /* package */ ChannelsWithMetadata transform(List<Channel> channels,
                                                 List<Membership> memberships,
                                                 List<User> directContacts) {
        final Map<String, User> userMap = new HashMap<>();
        for (User directContact : directContacts) {
            userMap.put(directContact.getId(), directContact);
        }

        final String currentUserId = mPrefs.getCurrentUserId();
        final String directChannelType = Channel.ChannelType.DIRECT.getRepresentation();

        final ChannelsWithMetadata channelsWithMetadata =
                new ChannelsWithMetadata(channels.size());
        for (Channel channel : channels) {
            if (channel.getType().equals(directChannelType)) {
                updateDirectChannelDisplayName(channel, userMap, currentUserId);
            }

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

    private void updateDirectChannelDisplayName(Channel channel, Map<String, User> contacts,
                                                String currentUserId) {
        final String channelId = channel.getName();
        final String otherUserId = extractOtherUserId(channelId, currentUserId);

        final User user = contacts.get(otherUserId);
        if (user != null) {
            channel.setDisplayName(User.getDisplayableName(user));
        }
    }

    private String extractOtherUserId(String channelId, String currentUserId) {
        return channelId.replace(currentUserId, "").replace("__", "");
    }
}


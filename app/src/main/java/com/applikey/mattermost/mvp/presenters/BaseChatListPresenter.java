package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.groups.Channel;
import com.applikey.mattermost.models.groups.ChannelWithMetadata;
import com.applikey.mattermost.models.groups.ChannelsWithMetadata;
import com.applikey.mattermost.models.groups.Membership;
import com.applikey.mattermost.mvp.views.ChannelsListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.web.Api;

import java.util.List;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseChatListPresenter extends SingleViewPresenter<ChannelsListView>
        implements ChatListPresenter {

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    /* package */ BaseChatListPresenter() {
        App.getComponent().inject(this);
    }

    /* package */ CompositeSubscription getSubscription() {
        return mSubscription;
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

    @Override
    public void unSubscribe() {
        mSubscription.clear();
    }
}


package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.data.MutableTuple;
import com.applikey.mattermost.models.groups.Channel;
import com.applikey.mattermost.models.groups.ChannelWithMetadata;
import com.applikey.mattermost.models.groups.ChannelsWithMetadata;
import com.applikey.mattermost.models.groups.Membership;
import com.applikey.mattermost.mvp.views.ChannelsListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ChannelsListPresenter extends SingleViewPresenter<ChannelsListView> {

    private final CompositeSubscription mSubscription = new CompositeSubscription();

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    public ChannelsListPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChannelsListView view = getView();
        mSubscription.add(
                Observable.zip(
                        mChannelStorage.list(),
                        mChannelStorage.listMembership(),
                        this::transform)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }

    private ChannelsWithMetadata transform(List<Channel> channels,
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

    public void unSubscribe() {
        mSubscription.clear();
    }
}

package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.arellomobile.mvp.MvpView;

public interface ChatListView extends MvpView {

    void displayInitialData(ChannelsWithMetadata channelsWithMetadata);
}

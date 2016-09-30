package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.groups.ChannelsWithMetadata;
import com.arellomobile.mvp.MvpView;

public interface ChannelsListView extends MvpView {

    void displayInitialData(ChannelsWithMetadata channelsWithMetadata);
}

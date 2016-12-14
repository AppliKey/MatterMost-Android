package com.applikey.mattermost.listeners;

import com.applikey.mattermost.models.channel.Channel;


public interface OnLoadAdditionalDataListener {

    void onLoadAdditionalData(Channel channel, int position);
}

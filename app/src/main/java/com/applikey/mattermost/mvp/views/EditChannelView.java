package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

public interface EditChannelView extends BaseEditChannelView {

    void showChannelData(Channel channel);

    void onChannelUpdated();
}

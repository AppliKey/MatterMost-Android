package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;

import java.util.List;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

public interface EditChannelView extends BaseEditChannelView {

    void showChannelData(Channel channel);

    void showMembers(List<User> users);

    void onChannelUpdated();

    void onChannelDeleted();
}

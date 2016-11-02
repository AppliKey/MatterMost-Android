package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;

import java.util.List;

/**
 * @author Anatoliy Chub
 */

public interface SearchUserView extends MvpView{

    void displayData(List<User> users);

    void startChatActivity(Channel channel);

    void showLoading(boolean show);

    void clearData();

}

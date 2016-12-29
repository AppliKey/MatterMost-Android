package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = SingleStateStrategy.class)
public interface MessageDetailsView extends MvpView {

    void setAuthorInfo(User user);

    void setPostInfo(Post post);

    void setUserStatus(User user);

    @StateStrategyType(value = SkipStrategy.class)
    void startChatView(Channel channel);

}

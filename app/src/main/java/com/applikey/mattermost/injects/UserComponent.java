package com.applikey.mattermost.injects;

import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.fragments.BaseChatListFragment;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.ChannelListPresenter;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.presenters.DirectChatListPresenter;
import com.applikey.mattermost.mvp.presenters.GroupListPresenter;
import com.applikey.mattermost.mvp.presenters.UnreadChatListPresenter;

import dagger.Subcomponent;

@PerUser
@Subcomponent(modules = UserModule.class)
public interface UserComponent {

    void inject(ChatActivity activity);

    void inject(BaseChatListPresenter presenter);

    void inject(ChannelListPresenter presenter);

    void inject(GroupListPresenter presenter);

    void inject(DirectChatListPresenter presenter);

    void inject(UnreadChatListPresenter presenter);

    void inject(BaseChatListFragment fragment);

    void inject(CreateChannelPresenter createChannelPresenter);

    @Subcomponent.Builder
    interface Builder {
        UserComponent.Builder userModule(UserModule userModule);

        UserComponent build();
    }
}

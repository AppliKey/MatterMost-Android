package com.applikey.mattermost.injects;

import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.fragments.BaseChatListFragment;
import com.applikey.mattermost.mvp.presenters.*;
import dagger.Subcomponent;

@PerUser
@Subcomponent(modules = UserModule.class)
public interface UserComponent {

    void inject(ChatActivity chatActivity);

    void inject(BaseChatListPresenter presenter);

    void inject(ChannelListPresenter presenter);

    void inject(GroupListPresenter presenter);

    void inject(DirectChatListPresenter presenter);

    void inject(UnreadChatListPresenter presenter);

    void inject(BaseChatListFragment fragment);

    void inject(CreateChannelPresenter presenter);

    void inject(SettingsPresenter presenter);

    @Subcomponent.Builder
    interface Builder {
        UserComponent.Builder userModule(UserModule userModule);

        UserComponent build();
    }
}

package com.applikey.mattermost.injects;

import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.activities.DrawerActivity;
import com.applikey.mattermost.fragments.BaseChatListFragment;
import com.applikey.mattermost.fragments.SearchFragment;
import com.applikey.mattermost.mvp.presenters.AddedMembersPresenter;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.presenters.ChannelDetailsPresenter;
import com.applikey.mattermost.mvp.presenters.ChannelListPresenter;
import com.applikey.mattermost.mvp.presenters.ChatListScreenPresenter;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.presenters.DirectChatListPresenter;
import com.applikey.mattermost.mvp.presenters.FindMoreChannelsPresenter;
import com.applikey.mattermost.mvp.presenters.GroupListPresenter;
import com.applikey.mattermost.mvp.presenters.InviteNewMemberPresenter;
import com.applikey.mattermost.mvp.presenters.MessageDetailsPresenter;
import com.applikey.mattermost.mvp.presenters.NavigationPresenter;
import com.applikey.mattermost.mvp.presenters.SearchAllPresenter;
import com.applikey.mattermost.mvp.presenters.SearchChannelPresenter;
import com.applikey.mattermost.mvp.presenters.SearchChatPresenter;
import com.applikey.mattermost.mvp.presenters.SearchMessagePresenter;
import com.applikey.mattermost.mvp.presenters.SearchUserPresenter;
import com.applikey.mattermost.mvp.presenters.SettingsPresenter;
import com.applikey.mattermost.mvp.presenters.UnreadChatListPresenter;
import com.applikey.mattermost.mvp.presenters.UserProfilePresenter;
import com.applikey.mattermost.mvp.presenters.edit.EditProfilePresenter;
import com.applikey.mattermost.mvp.views.BaseEditChannelView;
import com.applikey.mattermost.platform.socket.WebSocketService;

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

    void inject(CreateChannelPresenter presenter);

    void inject(BaseEditChannelPresenter<BaseEditChannelView> presenter);

    void inject(SettingsPresenter presenter);

    void inject(WebSocketService service);

    void inject(ChannelDetailsPresenter presenter);

    void inject(NavigationPresenter presenter);

    void inject(UserProfilePresenter presenter);

    void inject(SearchUserPresenter presenter);

    void inject(SearchChannelPresenter presenter);

    void inject(SearchAllPresenter presenter);

    void inject(SearchChatPresenter presenter);

    void inject(ChatListScreenPresenter presenter);

    void inject(ChatPresenter presenter);

    void inject(AddedMembersPresenter presenter);

    void inject(EditProfilePresenter presenter);

    void inject(SearchMessagePresenter presenter);

    void inject(SearchFragment searchFragment);

    void inject(FindMoreChannelsPresenter presenter);

    void inject(DrawerActivity drawerActivity);

    void inject(MessageDetailsPresenter presenter);

    void inject(InviteNewMemberPresenter presenter);

    @Subcomponent.Builder
    interface Builder {

        UserComponent.Builder userModule(UserModule userModule);

        UserComponent build();

    }
}

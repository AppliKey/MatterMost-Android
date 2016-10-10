package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.fragments.BaseChatListFragment;
import com.applikey.mattermost.fragments.BaseFragment;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.ChannelListPresenter;
import com.applikey.mattermost.mvp.presenters.ChatListScreenPresenter;
import com.applikey.mattermost.mvp.presenters.ChooseServerPresenter;
import com.applikey.mattermost.mvp.presenters.ChooseTeamPresenter;
import com.applikey.mattermost.mvp.presenters.DirectChatListPresenter;
import com.applikey.mattermost.mvp.presenters.GroupListPresenter;
import com.applikey.mattermost.mvp.presenters.LogInPresenter;
import com.applikey.mattermost.mvp.presenters.RestorePasswordPresenter;
import com.applikey.mattermost.mvp.presenters.UnreadChatListPresenter;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;

import dagger.Component;

@PerApp
@Component(modules = {
        GlobalModule.class
})
public interface ApplicationComponent {

    // Components
    void inject(BaseChatListFragment fragment);

    void inject(BaseFragment fragment);

    void inject(BaseActivity baseActivity);

    void inject(ChooseServerActivity baseActivity);

    void inject(App app);

    //Presenters
    void inject(LogInPresenter presenter);

    void inject(ChooseServerPresenter presenter);

    void inject(ChooseTeamPresenter presenter);

    void inject(RestorePasswordPresenter presenter);

    void inject(ChatListScreenPresenter presenter);

    void inject(ChannelListPresenter presenter);

    void inject(GroupListPresenter presenter);

    void inject(DirectChatListPresenter presenter);

    void inject(BaseChatListPresenter presenter);

    void inject(UnreadChatListPresenter presenter);

    // Storages
    void inject(TeamStorage storage);

    void inject(ChannelStorage storage);

    void inject(UserStorage storage);
}

package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.fragments.BaseFragment;
import com.applikey.mattermost.mvp.presenters.ChooseServerPresenter;
import com.applikey.mattermost.mvp.presenters.LogInPresenter;
import com.applikey.mattermost.storage.Db;
import com.applikey.mattermost.storage.TeamStorage;

import dagger.Component;

@PerApp
@Component(modules = {
        GlobalModule.class
})
public interface ApplicationComponent {

    // Components
    void inject(BaseFragment fragment);

    void inject(BaseActivity baseActivity);

    void inject(ChooseServerActivity baseActivity);

    void inject(App app);

    //Presenters
    void inject(LogInPresenter presenter);

    void inject(ChooseServerPresenter presenter);

    // Storages
    void inject(TeamStorage storage);
}

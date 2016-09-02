package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.activities.SampleActivity;
import com.applikey.mattermost.fragments.BaseFragment;

import dagger.Component;

@PerApp
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        GlobalModule.class
})
public interface ApplicationComponent {

    void inject(BaseFragment fragment);

    void inject(BaseActivity baseActivity);

    void inject(SampleActivity baseActivity);

    void inject(ChooseServerActivity baseActivity);

    void inject(App app);
}

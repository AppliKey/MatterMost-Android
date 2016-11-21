package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChooseServerActivity;
import com.applikey.mattermost.fragments.BaseFragment;
import com.applikey.mattermost.gcm.GcmMessageHandler;
import com.applikey.mattermost.gcm.RegistrationIntentService;
import com.applikey.mattermost.mvp.presenters.*;

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

    // Presenters
    void inject(SplashPresenter presenter);

    void inject(LogInPresenter presenter);

    void inject(ChooseServerPresenter presenter);

    void inject(ChooseTeamPresenter presenter);

    void inject(RestorePasswordPresenter presenter);

    void inject(InviteNewMemberPresenter presenter);

    // Services
    void inject(RegistrationIntentService service);

    void inject(GcmMessageHandler service);

    // Components
    UserComponent.Builder userComponentBuilder();
}

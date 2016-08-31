package com.applikey.skeletonproject.injects;


import com.applikey.skeletonproject.App;
import com.applikey.skeletonproject.activities.BaseActivity;
import com.applikey.skeletonproject.activities.MainActivity;
import com.applikey.skeletonproject.fragments.BaseFragment;

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

    void inject(MainActivity baseActivity);

    void inject(App app);
}

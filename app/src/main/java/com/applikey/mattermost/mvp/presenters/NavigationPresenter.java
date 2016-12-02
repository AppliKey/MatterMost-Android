package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Subscription;

@InjectViewState
public class NavigationPresenter extends BasePresenter<NavigationView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    Prefs mPrefs;

    public NavigationPresenter() {
        App.getUserComponent().inject(this);
    }

    public void createNewChannel() {
        getViewState().startChannelCreating();
    }

    public String getTeamName() {
        return mPrefs.getCurrentTeamName();
    }

    public void retrieveUser() {
        final Subscription subscription =
                mUserStorage.getMe()
                        .subscribe(getViewState()::onUserRetrieve, Throwable::printStackTrace);

        mSubscription.add(subscription);
    }
}

package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import static com.applikey.mattermost.activities.DrawerActivity.ITEM_FIND_MORE_CHANNELS;
import static com.applikey.mattermost.activities.DrawerActivity.ITEM_INVITE_MEMBER;
import static com.applikey.mattermost.activities.DrawerActivity.ITEM_SETTINGS;

@InjectViewState
public class NavigationPresenter extends BasePresenter<NavigationView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    Prefs mPrefs;

    @Inject
    ErrorHandler mErrorHandler;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        retrieveUser();
    }

    public NavigationPresenter() {
        App.getUserComponent().inject(this);
    }

    public void createNewChannel() {
        getViewState().startChannelCreating();
    }

    public String getTeamName() {
        return mPrefs.getCurrentTeamName();
    }

    public void onItemClick(int id) {
        switch (id) {
            case ITEM_FIND_MORE_CHANNELS:
                getViewState().findMoreChannels();
                break;
            case ITEM_INVITE_MEMBER:
                getViewState().startInviteNewMember();
                break;
            case ITEM_SETTINGS:
                getViewState().startSettings();
                break;
        }
    }

    private void retrieveUser() {
        mUserStorage.getMe()
                .compose(bindToLifecycle().forSingle())
                .subscribe(this::onUserLoaded, mErrorHandler::handleError);
    }

    private void onUserLoaded(User user) {
        getViewState().setUserInfo(user);
        getViewState().setTeamName(mPrefs.getCurrentTeamName());
    }
}

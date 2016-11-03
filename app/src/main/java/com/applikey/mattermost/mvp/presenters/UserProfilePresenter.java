package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.UserProfileView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

@InjectViewState
public class UserProfilePresenter extends BasePresenter<UserProfileView> {

    private static final String TAG = UserProfilePresenter.class.getSimpleName();

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    private User mUser;

    //temp field, remove after implement Make favorite logic
    private boolean mIsFavorite;

    public UserProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String userId) {
        final UserProfileView view = getViewState();

        mSubscription.add(mUserStorage.getDirectProfile(userId)
                .doOnNext(user -> mUser = user)
                .subscribe(view::showBaseDetails, ErrorHandler::handleError));
    }

    //TODO Implement favorite logic
    public void toggleChannelFavorite() {
        getViewState().onMakeChannelFavorite(mIsFavorite = !mIsFavorite);
    }
}

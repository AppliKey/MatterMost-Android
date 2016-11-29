package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.UserProfileView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class UserProfilePresenter extends BasePresenter<UserProfileView>
        implements FavoriteablePresenter {

    private static final String TAG = UserProfilePresenter.class.getSimpleName();

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    private User mUser;
    private Channel mChannel;

    public UserProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String userId) {
        final UserProfileView view = getViewState();

        final Subscription subscribe = mUserStorage.getDirectProfile(userId)
                .doOnNext(user -> mUser = user)
                .doOnNext(view::showBaseDetails)
                .flatMap(user -> mChannelStorage.directChannel(userId))
                .doOnNext(channel -> mChannel = channel)
                .map(Channel::isFavorite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onMakeFavorite, mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    @Override
    public void toggleFavorite() {
        boolean state = !mChannel.isFavorite();

        mChannelStorage.setFavorite(mChannel, state);
        getViewState().onMakeFavorite(state);
    }

    //TODO Create direct chat
    public void sendDirectMessage() {
        final Subscription subscribe = mChannelStorage.directChannel(mUser.getId())
                .first()
                .subscribe(channel -> getViewState().openDirectChannel(channel),
                           mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }
}

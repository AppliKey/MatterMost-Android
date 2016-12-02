package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
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

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    private String mUserId;
    private Channel mChannel;

    public UserProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String userId) {
        mUserId = userId;
        final UserProfileView view = getViewState();

        final Subscription subscribe = mUserStorage.getDirectProfile(userId)
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
        final boolean state = !mChannel.isFavorite();

        mChannelStorage.setFavorite(mChannel.getId(), state);
        getViewState().onMakeFavorite(state);
    }

    //TODO Create direct chat
    public void sendDirectMessage() {
        final Subscription subscribe = mChannelStorage.directChannel(mUserId)
                .subscribe(channel -> getViewState().openDirectChannel(channel), mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }
}

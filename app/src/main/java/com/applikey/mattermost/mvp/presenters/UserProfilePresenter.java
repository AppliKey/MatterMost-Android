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

    private final String mUserId;
    private Channel mChannel;

    public UserProfilePresenter(String userId) {
        App.getUserComponent().inject(this);
        mUserId = userId;
    }

    @Override
    public void toggleFavorite() {
        final boolean state = !mChannel.isFavorite();

        mChannelStorage.setFavorite(mChannel.getId(), state);
        getViewState().onMakeFavorite(state);
    }

    //TODO Create direct chat
    public void sendDirectMessage() {
        mChannelStorage.directChannel(mUserId)
                .compose(bindToLifecycle())
                .subscribe(channel -> getViewState().openDirectChannel(channel), mErrorHandler::handleError);
    }

    public void onMenuSet() {
        setInitialData();
    }

    private void setInitialData() {
        mUserStorage.getDirectProfile(mUserId)
                .compose(bindToLifecycle())
                .doOnNext(user -> getViewState().showBaseDetails(user))
                .flatMap(user -> mChannelStorage.directChannel(mUserId))
                .doOnNext(channel -> mChannel = channel)
                .map(Channel::isFavorite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favorite -> getViewState().onMakeFavorite(favorite), mErrorHandler::handleError);
    }
}

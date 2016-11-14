package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.manager.metadata.MetaDataManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
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

    private static final String TAG = UserProfilePresenter.class.getSimpleName();

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    MetaDataManager mMetaDataManager;

    private User mUser;
    private Channel mChannel;

    private boolean mIsFavorite;

    public UserProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String userId) {
        final UserProfileView view = getViewState();

        mSubscription.add(mUserStorage.getDirectProfile(userId)
                .doOnNext(user -> mUser = user)
                .doOnNext(view::showBaseDetails)
                .flatMap(user -> mChannelStorage.directChannel(userId))
                .doOnNext(channel -> mChannel = channel)
                .flatMap(channel -> mMetaDataManager.isFavoriteChannel(channel.getId()))
                .doOnNext(favorite -> mIsFavorite = favorite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onMakeFavorite, mErrorHandler::handleError));
    }

    @Override
    public void toggleFavorite() {
        Log.d(TAG, "toggleFavorite: " + !mIsFavorite);
        mSubscription.add(mMetaDataManager.setFavoriteChannel(mChannel.getId(), !mIsFavorite)
                .subscribe());
    }

    //TODO Create direct chat
    public void sendDirectMessage() {
        mSubscription.add(mChannelStorage.directChannel(mUser.getId())
                .first()
                .subscribe(channel -> getViewState().openDirectChannel(channel), mErrorHandler::handleError));
    }

}

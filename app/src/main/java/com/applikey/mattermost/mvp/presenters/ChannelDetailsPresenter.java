package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChannelDetailsPresenter extends BasePresenter<ChannelDetailsView>
        implements FavoriteablePresenter {

    private static final String TAG = ChannelDetailsPresenter.class.getSimpleName();

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    Prefs mPrefs;

    private Channel mChannel;

    public ChannelDetailsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        mSubscription.add(mChannelStorage.channelById(channelId)
                                  .doOnNext(channel -> mChannel = channel)
                                  .doOnNext(channel -> getViewState().showBaseDetails(channel))
                                  .doOnNext(channel -> getViewState().onMakeFavorite(channel.isFavorite()))
                                  .map(Channel::getUsers)
                                  .subscribe(users -> getViewState().showMembers(users),
                                             mErrorHandler::handleError));
    }

    @Override
    public void toggleFavorite() {
        final boolean state = !mChannel.isFavorite();

        mChannelStorage.setFavorite(mChannel.getId(), state);
        getViewState().onMakeFavorite(state);
    }

    public void onEditChannel() {
        getViewState().openEditChannel(mChannel);
    }

    public void leaveChannel() {
        final ChannelDetailsView view = getViewState();
        view.showProgress(true);
        mSubscription.add(mApi.leaveChannel(mPrefs.getCurrentTeamId(), mChannel.getId())
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .toObservable()
                                  .doOnNext(aVoid -> mChannelStorage.removeChannelAsync(mChannel))
                                  .subscribe(aVoid -> {
                                      view.showProgress(false);
                                      view.backToMainActivity();
                                  }, throwable -> {
                                      throwable.printStackTrace();
                                      view.showProgress(false);
                                      mErrorHandler.handleError(throwable);
                                  }));
    }

}

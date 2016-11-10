package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchUserTextChanged;
import com.applikey.mattermost.models.channel.DirectChannelRequest;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.SearchUserView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.ObjectNotFoundException;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class SearchUserPresenter extends BasePresenter<SearchUserView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ErrorHandler mErrorHandler;

    public SearchUserPresenter() {
        App.getUserComponent().inject(this);
        mEventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    public void getData(String text) {
        final SearchUserView view = getViewState();
        mSubscription.add(
                mUserStorage.searchUsers(text)
                        .first()
                        .flatMap(Observable::from)
                        .filter(user -> !TextUtils.equals(user.getId(), mCurrentUserId))
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayData, mErrorHandler::handleError));
    }

    public void handleUserClick(User user) {
        final SearchUserView view = getViewState();
        mSubscription.add(mChannelStorage.getChannel(user.getId())
                .observeOn(AndroidSchedulers.mainThread())
                // TODO CODE SMELLS
                .doOnError(throwable -> {
                    if (throwable instanceof ObjectNotFoundException) {
                        createChannel(user);
                    }
                })
                .subscribe(view::startChatView, mErrorHandler::handleError));
    }

    @Subscribe
    void on(SearchUserTextChanged event) {
        final SearchUserView view = getViewState();
        view.clearData();
        getData(event.getText());
    }

    private void createChannel(User user) {
        final SearchUserView view = getViewState();
        view.showLoading(true);
        mTeamStorage.getChosenTeam()
                .observeOn(Schedulers.io())
                .flatMap(team -> mApi.createChannel(team.getId(),
                        new DirectChannelRequest(user.getId())),
                        (team, channel) -> channel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.saveChannel(channel))
                .subscribe(channel -> {
                    view.startChatView(channel);
                    view.showLoading(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    view.showLoading(false);
                });
    }
}

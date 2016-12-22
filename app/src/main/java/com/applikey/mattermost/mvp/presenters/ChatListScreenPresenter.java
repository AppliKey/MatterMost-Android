package com.applikey.mattermost.mvp.presenters;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.fragments.DirectChatListFragment;
import com.applikey.mattermost.fragments.FavoriteChatListFragment;
import com.applikey.mattermost.fragments.GroupListFragment;
import com.applikey.mattermost.fragments.UnreadChatListFragment;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.init.InitLoadResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.StartupFetchResult;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.PreferenceStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.SettingsManager;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChatListScreenPresenter extends BasePresenter<ChatListScreenView> {

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    PostStorage mPostStorage;

    @Inject
    SettingsManager mSettingsManager;

    @Inject
    PreferenceStorage mPreferenceStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    private boolean mLastUnreadTabState;

    public ChatListScreenPresenter() {
        App.getUserComponent().inject(this);
    }

    public void preloadChannel(String channelId) {
        final Subscription subscription =
                Observable.amb(mChannelStorage.channelById(channelId), mTeamStorage.getChosenTeam()
                        .flatMap(team -> mApi.getChannelById(team.getId(), channelId).subscribeOn(Schedulers.io())))
                        .observeOn(AndroidSchedulers.mainThread())
                        .first()
                        .subscribe(channel -> {
                            getViewState().onChannelLoaded(channel);
                        }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    public void loadInitInfo() {
        final Subscription subscription = mApi.getInitialLoad()
                .subscribeOn(Schedulers.io())
                .map(InitLoadResponse::getPreferences)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(preferences -> mPreferenceStorage.save(preferences))
                .observeOn(Schedulers.io())
                .flatMap(v -> mApi.getMe())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mUserStorage::save, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    public void initPages() {
        mLastUnreadTabState = shouldShowUnreadTab();
        getViewState().initViewPager(initTabs(mLastUnreadTabState));
    }

    public void checkSettingChanges() {
        final boolean shouldShowUnreadTab = shouldShowUnreadTab();
        if (mLastUnreadTabState != shouldShowUnreadTab) {
            mLastUnreadTabState = shouldShowUnreadTab;
            getViewState().initViewPager(initTabs(shouldShowUnreadTab));
        }
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d("ChatListPresenter", "onFirstViewAttach: ");
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .doOnNext(team -> getViewState().setToolbarTitle(team.getDisplayName()))
                .map(Team::getId)
                .first()
                .toSingle()
                .flatMap(this::fetchStartup)
                .doOnSuccess(this::fetchUserStatus)
                .subscribe(v -> {
                }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private Single<StartupFetchResult> fetchStartup(String teamId) {
        Log.d("ChatListPresenter", "fetchStartup: start");
        return Single.zip(mApi.listChannels(teamId), mApi.getTeamProfiles(teamId),
                          (channelResponse, contacts) -> transform(channelResponse, contacts, teamId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(startupFetchResult -> {
                    Log.d("ChatListPresenter", "fetchStartup: ");
                    mUserStorage.save(startupFetchResult.getDirectProfiles());
                    mChannelStorage.saveChannelResponse(
                            startupFetchResult.getChannelResponse(),
                            startupFetchResult.getDirectProfiles());
                });

    }

    public boolean shouldShowUnreadTab() {
        return mSettingsManager.shouldShowUnreadMessages();
    }

    private void fetchUserStatus(StartupFetchResult response) {
        final Set<String> keys = response.getDirectProfiles().keySet();

        // TODO: Remove v3.3 API support
        mApi.getUserStatusesCompatible(keys.toArray(new String[] {}))
                .onErrorResumeNext(throwable -> mApi.getUserStatuses())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(userStatusResponse -> mUserStorage.saveUsersStatuses(
                        response.getDirectProfiles(), userStatusResponse))
                .subscribe(v -> {
                }, mErrorHandler::handleError);
    }

    private StartupFetchResult transform(ChannelResponse channelResponse,
            Map<String, User> contacts, String teamId) {
        return new StartupFetchResult(channelResponse, contacts, teamId);
    }

    private List<Fragment> initTabs(boolean shouldShowUnreadTab) {
        final List<Fragment> tabs = new ArrayList<>();
        if (shouldShowUnreadTab) {
            tabs.add(UnreadChatListFragment.newInstance());
        }
        tabs.add(FavoriteChatListFragment.newInstance());
        tabs.add(ChannelListFragment.newInstance());
        tabs.add(GroupListFragment.newInstance());
        tabs.add(DirectChatListFragment.newInstance());
        return tabs;
    }
}

package com.applikey.mattermost.mvp.presenters;

import android.support.v4.app.Fragment;

import com.applikey.mattermost.App;
import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.fragments.DirectChatListFragment;
import com.applikey.mattermost.fragments.FavoriteChatListFragment;
import com.applikey.mattermost.fragments.GroupListFragment;
import com.applikey.mattermost.fragments.UnreadChatListFragment;
import com.applikey.mattermost.models.init.InitLoadResponse;
import com.applikey.mattermost.models.team.Team;
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
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
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

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        loadInitInfo();
    }

    public void preloadChannel(String channelId) {
        Observable.amb(mChannelStorage.channelById(channelId), mTeamStorage.getChosenTeam()
                .compose(bindToLifecycle())
                .flatMap(team -> mApi.getChannelById(team.getId(), channelId)
                        .subscribeOn(Schedulers.io())
                        .toObservable()))
                .observeOn(AndroidSchedulers.mainThread())
                .toSingle()
                .subscribe(channel -> getViewState().onChannelLoaded(channel), mErrorHandler::handleError);
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

    public boolean shouldShowUnreadTab() {
        return mSettingsManager.shouldShowUnreadMessages();
    }

    private void loadInitInfo() {
        mTeamStorage.getChosenTeam()
                .compose(bindToLifecycle())
                .doOnNext(team -> getViewState().setToolbarTitle(team.getDisplayName()))
                .map(Team::getId)
                .first()
                .toSingle()
                .flatMap(this::fetchStartup)
                .doOnSuccess(this::fetchUserStatus)
                .subscribe(v -> {
                }, mErrorHandler::handleError);

        mApi.getInitialLoad()
                .compose(bindToLifecycle().forSingle())
                .subscribeOn(Schedulers.io())
                .map(InitLoadResponse::getPreferences)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(mPreferenceStorage::save)
                .observeOn(Schedulers.io())
                .flatMap(v -> mApi.getMe())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mUserStorage::save, mErrorHandler::handleError);
    }

    private Single<StartupFetchResult> fetchStartup(String teamId) {
        return Single.zip(mApi.listChannels(teamId), mApi.getTeamProfiles(teamId),
                          (channelResponse, contacts) -> new StartupFetchResult(channelResponse, contacts, teamId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(startupFetchResult -> {
                    mUserStorage.save(startupFetchResult.getDirectProfiles());
                    mChannelStorage.saveChannelResponse(
                            startupFetchResult.getChannelResponse(),
                            startupFetchResult.getDirectProfiles());
                });

    }

    private void fetchUserStatus(StartupFetchResult response) {
        final Set<String> keys = response.getDirectProfiles().keySet();

        // TODO: Remove v3.3 API support
        mApi.getUserStatusesCompatible(keys.toArray(new String[] {}))
                .compose(bindToLifecycle().forSingle())
                .onErrorResumeNext(throwable -> mApi.getUserStatuses())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(userStatusResponse -> mUserStorage.saveUsersStatuses(
                        response.getDirectProfiles(), userStatusResponse))
                .subscribe(v -> {
                }, mErrorHandler::handleError);
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

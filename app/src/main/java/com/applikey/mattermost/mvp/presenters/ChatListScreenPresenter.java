package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.StartupFetchResult;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.StorageDestroyer;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
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
    Lazy<StorageDestroyer> mStorageDestroyer;

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    public ChatListScreenPresenter() {
        App.getComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        final ChatListScreenView view = getViewState();

        final Subscription subscription = mTeamStorage.getChosenTeam()
                .doOnNext(team -> view.setToolbarTitle(team.getDisplayName()))
                .map(Team::getId)
                .flatMap(this::fetchStartup)
                .doOnNext(this::fetchLastMessages)
                .doOnNext(this::fetchUserStatus)
                .subscribe(v -> {
                }, ErrorHandler::handleError);
        mSubscription.add(subscription);
    }

    private Observable<StartupFetchResult> fetchStartup(String teamId) {
        return Observable.zip(mApi.listChannels(teamId), mApi.getTeamProfiles(teamId),
                (channelResponse, contacts) -> transform(channelResponse, contacts, teamId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(response -> mUserStorage.saveUsers(response.getDirectProfiles()))
                .doOnNext(response -> mChannelStorage.saveChannelResponse(response.getChannelResponse(),
                        response.getDirectProfiles()));

    }

    private void fetchLastMessages(StartupFetchResult response) {
        Observable.from(response.getChannelResponse().getChannels())
                .flatMap(channel -> mApi.getLastPost(response.getTeamId(), channel.getId())
                        .onErrorResumeNext(throwable -> null), this::transform)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.updateLastPost(channel))
                .subscribe();
    }

    private void fetchUserStatus(StartupFetchResult response) {
        final Set<String> keys = response.getDirectProfiles().keySet();

        // TODO: Remove v3.3 API support
        mApi.getUserStatusesCompatible(keys.toArray(new String[] {}))
                .onErrorResumeNext(throwable -> mApi.getUserStatuses())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(userStatusResponse -> {
                    mUserStorage.saveUsersStatuses(response.getDirectProfiles(), userStatusResponse);
                })
                .subscribe(v -> {
                }, ErrorHandler::handleError);
    }

    private Channel transform(Channel channel, PostResponse postResponse) {
        final Iterator<Post> posts = postResponse.getPosts().values().iterator();
        channel.setLastPost(posts.hasNext() ? posts.next() : null);
        return channel;
    }

    private StartupFetchResult transform(ChannelResponse channelResponse,
            Map<String, User> contacts, String teamId) {
        return new StartupFetchResult(channelResponse, contacts, teamId);
    }

    public void applyInitialViewState() {
        mSubscription.add(mTeamStorage.getChosenTeam().subscribe(team -> {
            getViewState().setToolbarTitle(team.getDisplayName());
        }, ErrorHandler::handleError));
    }

    public void logout() {
        mPrefs.setAuthToken(null);
        App.releaseUserComponent();
        mStorageDestroyer.get().deleteDatabase();
        getViewState().logout();
    }
}

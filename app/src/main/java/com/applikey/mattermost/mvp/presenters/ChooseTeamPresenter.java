package com.applikey.mattermost.mvp.presenters;

import android.content.res.Resources;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.web.StartupFetchResult;
import com.applikey.mattermost.mvp.views.ChooseTeamView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChooseTeamPresenter extends BasePresenter<ChooseTeamView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    ImagePathHelper mImagePathHelper;

    @Inject
    Resources mResources;

    public ChooseTeamPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChooseTeamView view = getViewState();
        mSubscription.add(mTeamStorage.listAll()
                .subscribe(view::displayTeams, throwable -> {
                    ErrorHandler.handleError(throwable);
                    getViewState().onFailure(throwable.getMessage());
                }));
    }

    public void chooseTeam(Team team) {
        mTeamStorage.setChosenTeam(team);
        final ChooseTeamView view = getViewState();
        // TODO: Remove v3.3 API support

        // Perform Startup Fetch
        mSubscription.add(Observable.zip(
                mApi.listChannels(team.getId()),
                mApi.getTeamProfiles(team.getId()),
                (StartupFetchResult::new))
                .subscribeOn(Schedulers.io())
                .flatMap(response -> fetchLastMessages(response, team.getId()))
                .flatMap(response -> {
                    final Set<String> keys = response.getDirectProfiles().keySet();

                    return mApi.getUserStatusesCompatible(keys.toArray(new String[] {}))
                            .onErrorResumeNext(throwable -> {
                                return mApi.getUserStatuses();
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(userStatusResponse -> {
                                mUserStorage.saveUsers(response.getDirectProfiles(),
                                        userStatusResponse);
                                mChannelStorage.saveChannelResponse(response.getChannelResponse(),
                                        response.getDirectProfiles());
                            });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.onTeamChosen();
                }));
    }

    private Observable<StartupFetchResult> fetchLastMessages(StartupFetchResult response, String teamId) {
        return Observable.from(response.getChannelResponse().getChannels())
                .flatMap(channel -> mApi.getLastPost(teamId, channel.getId()), this::transform)
                .toList()
                .map(channels -> response);
    }

    private Channel transform(Channel channel, PostResponse postResponse) {
        Iterator<Post> posts = postResponse.getPosts().values().iterator();
        String lastMessage = posts.hasNext() ? posts.next().getMessage() : null;
        channel.setPreviewMessage(lastMessage);
        return channel;
    }
}

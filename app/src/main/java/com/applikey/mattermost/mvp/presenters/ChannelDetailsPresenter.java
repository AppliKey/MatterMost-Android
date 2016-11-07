package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.channel.MemberInfo;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChannelDetailsPresenter extends BasePresenter<ChannelDetailsView> {

    private static final String TAG = ChannelDetailsPresenter.class.getSimpleName();

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    ErrorHandler mErrorHandler;

    private Channel mChannel;

    //temp field, remove after implement Make favorite logic
    private boolean mIsFavorite;

    public ChannelDetailsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChannelDetailsView view = getViewState();

        mSubscription.add(mChannelStorage.channel(channelId)
                .doOnNext(channel -> mChannel = channel)
                .subscribe(view::showBaseDetails, mErrorHandler::handleError));

        mSubscription.add(
                mTeamStorage.getChosenTeam()
                        .first()
                        .map(Team::getId)
                        .flatMap(teamId -> mApi.getChannelExtra(teamId, channelId).subscribeOn(Schedulers.io()))
                        .map(ExtraInfo::getMembers)
                        .flatMap(Observable::from)
                        .map(MemberInfo::getId)
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(ids -> mUserStorage.findUsers(ids))
                        .subscribe(view::showMembers, mErrorHandler::handleError));
    }

    //TODO Implement favorite logic
    public void toggleChannelFavorite() {
        getViewState().onMakeChannelFavorite(mIsFavorite = !mIsFavorite);
    }
}

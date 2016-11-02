package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

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

    private Channel mChannel;

    public ChannelDetailsPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChannelDetailsView view = getViewState();

        mSubscription.add(mChannelStorage.channel(channelId)
                .doOnNext(view::showBaseDetails)
                .subscribe(channel -> mChannel = channel, ErrorHandler::handleError));
/*        mSubscription.add(
                mTeamStorage.getChosenTeam()
                        .observeOn(Schedulers.io())
                        .map(Team::getId)
                        .flatMap(teamId -> mApi.getChannelExtra(teamId, channelId))
                        .map(ExtraInfo::getMembers)
                        .flatMap(Observable::from)
                        .map()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(users -> view.showMembers(users)));*/
    }
}

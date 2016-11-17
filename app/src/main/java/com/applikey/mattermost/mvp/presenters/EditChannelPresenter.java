package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelPurposeRequest;
import com.applikey.mattermost.models.channel.ChannelTitleRequest;
import com.applikey.mattermost.models.channel.CreatedChannel;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.arellomobile.mvp.InjectViewState;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.applikey.mattermost.utils.rx.RxUtils.doOnUi;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

@InjectViewState
public class EditChannelPresenter extends BaseEditChannelPresenter<EditChannelView> {

    private Channel mChannel;

    public EditChannelPresenter() {
        super();
    }

    public void getInitialData(String channelId) {
        final Subscription subscription = mChannelStorage.channelById(channelId)
                .first()
                .doOnNext(channel -> mChannel = channel)
                .subscribe(channel -> {
                    getViewState().showChannelData(channel);
                }, mErrorHandler::handleError);
        mSubscription.add(subscription);
    }


    public void updateChannel(String channelName, String channelDescription) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showEmptyChannelNameError();
            return;
        }

        final ChannelTitleRequest channelTitleRequest = new ChannelTitleRequest(mChannel);
        channelTitleRequest.setDisplayName(channelName);
        final ChannelPurposeRequest channelPurposeRequest = new ChannelPurposeRequest(
                mChannel.getId(), channelDescription);
        updateChannel(channelTitleRequest, channelPurposeRequest);
    }

    private void updateChannel(ChannelTitleRequest channelTitleRequest,
            ChannelPurposeRequest channelPurposeRequest) {
        mTeamStorage.getChosenTeam()
                .first()
                .observeOn(Schedulers.io())
                .map(Team::getId)
                .doOnNext(channelTitleRequest::setTeamId)
                .flatMap(teamId ->
                        mApi.updateChannelTitle(teamId, channelTitleRequest), CreatedChannel::new)
                .compose(doOnUi(createdChannel ->
                                mChannelStorage.saveChannel(createdChannel.getChannel()),
                        Schedulers.io()))
                .flatMap(createdChannel -> mApi.updateChannelPurpose(createdChannel.getTeamId(),
                        channelPurposeRequest),
                        (createdChannel, channel) ->
                                new CreatedChannel(createdChannel.getTeamId(), channel))
                .compose(doOnUi(createdChannel -> mChannelStorage.saveChannel(
                        createdChannel.getChannel()),
                        Schedulers.io()))
                .toCompletable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getViewState().onChannelUpdated(),
                        error -> getViewState().showError(mErrorHandler.getErrorMessage(error)));
    }

}

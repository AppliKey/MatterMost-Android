package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelPurposeRequest;
import com.applikey.mattermost.models.channel.ChannelTitleRequest;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.arellomobile.mvp.InjectViewState;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        String teamId = mPrefs.getCurrentTeamId();
        mApi.updateChannelTitle(teamId, channelTitleRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.saveChannel(channel))
                .observeOn(Schedulers.io())
                .flatMap(channel -> mApi.updateChannelPurpose(teamId, channelPurposeRequest))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> mChannelStorage.saveChannel(channel))
                .toCompletable()
                .subscribe(() -> getViewState().onChannelUpdated(),
                        error -> getViewState().showError(mErrorHandler.getErrorMessage(error)));
    }

}

package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelPurposeRequest;
import com.applikey.mattermost.models.channel.ChannelTitleRequest;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.arellomobile.mvp.InjectViewState;

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
        mSubscription.add(mChannelStorage.channelById(channelId)
                .doOnNext(channel -> mChannel = channel)
                .subscribe(getViewState()::showChannelData, mErrorHandler::handleError));
    }


    public void updateChannel(String channelName, String channelDescription) {
        if (TextUtils.isEmpty(channelName)) {
            getViewState().showEmptyChannelNameError();
            return;
        }

        final ChannelTitleRequest channelTitleRequest = new ChannelTitleRequest(mChannel);
    }

    private void updateChannel(ChannelTitleRequest channelTitleRequest,
            ChannelPurposeRequest channelPurposeRequest) {

    }

}

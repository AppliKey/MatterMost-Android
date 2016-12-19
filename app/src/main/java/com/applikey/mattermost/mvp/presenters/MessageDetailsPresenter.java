package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.mvp.views.MessageDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

@InjectViewState
public class MessageDetailsPresenter extends BasePresenter<MessageDetailsView> {

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    PostStorage mPostStorage;

    public MessageDetailsPresenter() {
        App.getUserComponent().inject(this);
    }

    public void initMessage(String postId) {
        final MessageDetailsView view = getViewState();
        mPostStorage.get(postId)
                .flatMap(post -> mChannelStorage.getChannel(post.getChannelId())
                        .map(channel -> new Message(post, channel)))
                .flatMap(message -> mUserStorage.getDirectProfile(message.getPost().getUserId())
                       .toSingle()
                        .doOnSuccess(message::setUser)
                       .map(user -> message))
                .subscribe(view::initView, Throwable::printStackTrace);
    }

    public void onGoToDialogButtonClick(Channel channel) {
        final MessageDetailsView view = getViewState();
        view.startChatView(channel);
    }
}

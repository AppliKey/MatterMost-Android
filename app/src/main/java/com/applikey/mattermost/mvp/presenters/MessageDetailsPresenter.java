package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
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

    private final String mPostId;
    private Message mMessage;

    public MessageDetailsPresenter(String postId) {
        App.getUserComponent().inject(this);
        mPostId = postId;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        mPostStorage.get(mPostId)
                .compose(bindToLifecycle().forSingle())
                .flatMap(post -> mChannelStorage.getChannel(post.getChannelId())
                        .map(channel -> new Message(post, channel)))
                .subscribe(this::onMessageLoaded, Throwable::printStackTrace);
    }

    public void onGoToDialogButtonClick() {
        getViewState().startChatView(mMessage.getChannel());
    }

    private void onMessageLoaded(Message message) {
        mMessage = message;
        final Post post = message.getPost();
        final User author = post.getAuthor();
        getViewState().setPostInfo(post);
        getViewState().setAuthorInfo(author);
        getViewState().setUserStatus(author);
    }
}

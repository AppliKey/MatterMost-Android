package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.mvp.views.MessageDetailsView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class MessageDetailsPresenter extends BasePresenter<MessageDetailsView> {

    private static final String TAG = MessageDetailsPresenter.class.getSimpleName();
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
                .toObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(post -> mChannelStorage.getChannel(post.getChannelId()).toObservable(),
                         (post, channel) -> new Message(post, channel))
                // TODO: 15.12.16 FIX IT
                .flatMap(message -> mUserStorage.getDirectProfile(message.getPost().getUserId()),
                         (message, user) -> {
                             message.setUser(user);
                             return message;})
                .subscribe(view::initView, Throwable::printStackTrace);
    }

}

package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.PendingPost;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.rx.RxUtils;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChatPresenter extends BasePresenter<ChatView> {

    private static final int PAGE_SIZE = 60;
    private static final String CHANNEL_PREFIX = "#";
    private static final String DIRECT_PREFIX = "";

    @Inject
    PostStorage mPostStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    NotificationManager mNotificationManager;

    @Inject
    ErrorHandler mErrorHandler;

    private Channel mChannel;
    private String mTeamId;
    private AtomicInteger mMessageSendingCounter = new AtomicInteger(0);

    private boolean mFirstFetched = false;

    public ChatPresenter() {
        App.getUserComponent().inject(this);
        mTeamId = mPrefs.getCurrentTeamId();
    }

    public void getInitialData(String channelId) {
        final ChatView view = getViewState();

        updateLastViewedAt(channelId);

        final Subscription subscribe = mChannelStorage.channelById(channelId)
                .distinctUntilChanged()
                .doOnNext(channel -> mChannel = channel)
                .map(channel -> {
                    final String prefix = !mChannel.getType()
                            .equals(Channel.ChannelType.DIRECT.getRepresentation())
                            ? CHANNEL_PREFIX : DIRECT_PREFIX;
                    return prefix + channel.getDisplayName();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::showTitle, mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    public void loadMessages(String channelId) {
        final ChatView view = getViewState();

        final Subscription subscribe = mPostStorage.listByChannel(channelId)
                .first()
                .doOnNext(posts -> getViewState().showEmpty(posts.isEmpty()))
                .doOnNext(v -> fetchFirstPageWithClear(channelId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onDataReady, mErrorHandler::handleError);

        mSubscription.add(subscribe);

        listenPostsCount(channelId);
    }

    public void channelNameClick() {
        final ChatView view = getViewState();
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(mChannel.getType())) {
            view.openUserProfile(mChannel.getDirectCollocutor());
        } else {
            view.openChannelDetails(mChannel);
        }
    }

    public void fetchAfterRestart() {
        if (!mFirstFetched) {
            return;
        }
        fetchPage(0, mChannel.getId(), false);
    }

    public void fetchFirstPageWithClear(String channelId) {
        fetchPage(0, channelId, true);
    }

    public void fetchNextPage(int totalItems) {
        fetchPage(totalItems, mChannel.getId(), false);
    }

    public void deleteMessage(String channelId, Post post) {

        final Subscription subscribe = mApi.deletePost(mTeamId, channelId, post.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(posts -> mPostStorage.delete(post))
                .doOnSuccess(posts -> mChannel.setLastPost(null))
                .subscribe(posts -> mChannelStorage.updateLastPost(mChannel), mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    public void editMessage(String channelId, Post post, String newMessage) {
        final Post finalPost = mPostStorage.copyFromDb(post);
        finalPost.setMessage(newMessage);
        final Subscription subscribe = mApi.updatePost(mTeamId, channelId, finalPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> mPostStorage.update(finalPost), mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    public void sendMessage(String channelId, String message) {
        if (TextUtils.isEmpty(message.trim())) {
            return;
        }

        final ChatView view = getViewState();
        view.clearMessageInput();
        view.showLoading(true);
        mMessageSendingCounter.incrementAndGet();

        final String currentUserId = mPrefs.getCurrentUserId();
        final long createdAt = System.currentTimeMillis();
        final String pendingId = currentUserId + ":" + createdAt;

        final PendingPost pendingPost = new PendingPost(createdAt, currentUserId, channelId,
                                                        message, "", pendingId);

        final Subscription subscribe = mApi.createPost(mTeamId, channelId, pendingPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(post -> mChannelStorage.setLastViewedAt(channelId, post.getCreatedAt()))
                .doOnSuccess(post -> mChannelStorage.setLastPost(mChannel, post))
                .subscribe(result -> {
                    view.showLoading(mMessageSendingCounter.decrementAndGet() != 0);
                    view.onMessageSent(result.getCreatedAt());
                }, throwable -> {
                    mErrorHandler.handleError(throwable);
                    view.showLoading(mMessageSendingCounter.decrementAndGet() != 0);
                });

        mSubscription.add(subscribe);
    }

    // TODO: Refactor: duplicated code
    public void sendReplyMessage(String channelId, String message, String mRootId) {
        if (TextUtils.isEmpty(message.trim())) {
            return;
        }

        getViewState().clearMessageInput();

        final String currentUserId = mPrefs.getCurrentUserId();
        final long createdAt = System.currentTimeMillis();
        final String pendingId = currentUserId + ":" + createdAt;

        final PendingPost pendingPost = new PendingPost(createdAt, currentUserId, channelId,
                                                        message, "", pendingId, mRootId, mRootId);

        final Subscription subscribe = mApi.createPost(mTeamId, channelId, pendingPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(post -> mChannelStorage.setLastViewedAt(channelId, post.getCreatedAt()))
                .doOnSuccess(post -> mChannelStorage.setLastPost(mChannel, post))
                .subscribe(result -> getViewState().onMessageSent(result.getCreatedAt()), mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    public void joinToChannel(String channelId) {
        final Subscription subscription = mApi.joinToChannel(mTeamId, channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channel -> {
                    channel.setJoined(true);
                    mChannelStorage.save(channel);
                    getViewState().onChannelJoined();
                }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private void updateLastViewedAt(String channelId) {
        // Does not belong to UI
        final Subscription subscribe = mApi.updateLastViewedAt(mTeamId, channelId)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringResponse -> {}, mErrorHandler::handleError);

        mSubscription.add(subscribe);

        mChannelStorage.updateLastViewedAt(channelId);
        mNotificationManager.dismissNotification(channelId);
    }

    private void listenPostsCount(String channelId) {
        final Subscription subscription =
                mPostStorage.listByChannel(channelId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(realmResults -> {
                            getViewState().showEmpty(realmResults.isEmpty());
                        }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private void fetchPage(int totalItems, String channelId, boolean clear) {
        final ChatView view = getViewState();

        final Subscription subscription = mApi.getPostsPage(mTeamId, channelId, totalItems, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .switchIfEmpty(Observable.empty())
                .map(postResponse -> postResponse.getPosts().values())
                .map(ArrayList::new)
                .doOnNext(posts -> Collections.sort(posts, Post::COMPARATOR_BY_CREATE_AT))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.applyProgress(view::showProgress, view::hideProgress))
                .subscribe(posts -> {
                    if (clear) {
                        clearChat();
                    }
                    if (totalItems == 0 && !posts.isEmpty()) {
                        mChannelStorage.setLastPost(mChannel, posts.get(posts.size() - 1));
                    }
                    mFirstFetched = true;
                    mPostStorage.saveAll(posts);
                }, error -> {
                    mErrorHandler.handleError(error);
                });
        mSubscription.add(subscription);
    }

    private void clearChat() {
        mPostStorage.deleteAllByChannel(mChannel.getId());
    }
}

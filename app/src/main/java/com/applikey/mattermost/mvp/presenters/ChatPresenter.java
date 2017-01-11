package com.applikey.mattermost.mvp.presenters;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.PendingPost;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.Callback;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.utils.rx.RxUtils;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
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
    BearerTokenFactory mBearerTokenFactory;

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
        updateLastViewedAt(channelId);

        final Subscription subscribe = mChannelStorage.findByIdAndCopy(channelId)
                .distinctUntilChanged()
                .doOnNext(channel -> mChannel = channel)
                .map(channel -> {
                    final String prefix = !mChannel.getType()
                            .equals(Channel.ChannelType.DIRECT.getRepresentation())
                            ? CHANNEL_PREFIX : DIRECT_PREFIX;
                    return prefix + channel.getDisplayName();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(title -> {
                    getViewState().showTitle(title);
                    final Subscription sub = mPostStorage.listByChannel(channelId)
                            .first()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(p -> getViewState().onDataReady(p, false),
                                    mErrorHandler::handleError);
                    mSubscription.add(sub);
                }, mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    public void loadMessages(String channelId) {
        getViewState().showProgress();
        fetchFirstPage(channelId);
    }

    public void openChannelDetails() {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(mChannel.getType())) {
            getViewState().openUserProfile(mChannel.getDirectCollocutor());
        } else {
            getViewState().openChannelDetails(mChannel);
        }
    }

    public void fetchAfterRestart() {
        if (!mFirstFetched) {
            return;
        }
        fetchPage(0, mChannel.getId(), true, false);
    }

    private void fetchFirstPage(String channelId) {
        fetchPage(0, channelId, true, true);
    }

    public void fetchNextPage(int totalItems) {
        if (!mFirstFetched) {
            return;
        }
        fetchPage(totalItems, mChannel.getId(), false, false);
    }

    public void deleteMessage(String channelId, Post post) {
        if (!post.isSent()) {
            mPostStorage.delete(post);
            mChannelStorage.updateLastPost(mChannel);
        } else {
            final Subscription subscribe = mApi.deletePost(mTeamId, channelId, post.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(posts -> mPostStorage.delete(post))
                    .doOnSuccess(posts -> mChannel.setLastPost(null))
                    .subscribe(posts -> mChannelStorage.updateLastPost(mChannel), mErrorHandler::handleError);

            mSubscription.add(subscribe);
        }
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

    public void sendMessage(String channelId, String message, @Nullable String postId) {
        if (TextUtils.isEmpty(message.trim())) {
            return;
        }

        getViewState().clearMessageInput();
        getViewState().showLoading(true);
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
                .doOnSuccess(post -> {
                    if (!TextUtils.isEmpty(postId)) {
                        mPostStorage.delete(postId);
                    }
                })
                .subscribe(result -> {
                    getViewState().showLoading(mMessageSendingCounter.decrementAndGet() != 0);
                    getViewState().onMessageSent(result.getCreatedAt());
                }, throwable -> {
                    final Post post = new Post.Builder().channelId(mChannel.getId())
                            .createdAt(createdAt)
                            .id(postId == null ? UUID.randomUUID().toString() : postId)
                            .message(message)
                            .userId(currentUserId)
                            .sent(false)
                            .build();
                    mChannelStorage.setLastPost(mChannel, post);
                    mErrorHandler.handleError(throwable);
                    getViewState().showLoading(mMessageSendingCounter.decrementAndGet() != 0);
                });

        mSubscription.add(subscribe);
    }

    public void sendMessage(String channelId, String messsage) {
        sendMessage(channelId, messsage, null);
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

    public void joinChannel(String channelId) {
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

    public void requestDownload(String url) {
        final String token = mBearerTokenFactory.getBearerTokenString();

        final DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        final String name = StringUtil.extractFileName(url);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.addRequestHeader(Constants.AUTHORIZATION_HEADER, token);

        getViewState().downloadFile(request, name);
    }

    private void updateLastViewedAt(String channelId) {
        final Subscription subscribe = mApi.updateLastViewedAt(mTeamId, channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable()
                .subscribe(() -> {
                }, mErrorHandler::handleError);

        mSubscription.add(subscribe);

        mChannelStorage.updateLastViewedAt(channelId);
        mNotificationManager.dismissNotification(channelId);
    }

    private void listenPostsCount(String channelId) {
        final Subscription subscription =
                mPostStorage.listByChannel(channelId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(realmResults -> getViewState().showEmpty(realmResults.isEmpty()),
                                mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private void fetchPage(int totalItems, String channelId, boolean clear, boolean initializeView) {
        final Subscription subscription = mApi.getPostsPage(mTeamId, channelId, totalItems, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .switchIfEmpty(Observable.empty())
                .map(postResponse -> postResponse.getPosts().values())
                .map(ArrayList::new)
                .doOnNext(posts -> Collections.sort(posts, Post.COMPARATOR_BY_CREATE_AT))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.applyProgress(getViewState()::showProgress, getViewState()::hideProgress))
                .subscribe(posts -> savePosts(channelId, totalItems, posts, initializeView, clear),
                        mErrorHandler::handleError);
        mSubscription.add(subscription);
    }

    private void savePosts(String channelId, int totalItems, ArrayList<Post> posts,
                           boolean initializeView, boolean clear) {
        final Callback callback = () -> {

            if (totalItems == 0 && !posts.isEmpty()) {
                mChannelStorage.setLastPost(mChannel, posts.get(posts.size() - 1));
            }

            if (initializeView) {
                getViewState().subscribeForMessageChanges();
            }
            listenPostsCount(channelId);
            getViewState().hideProgress();
            mFirstFetched = true;
        };
        if (clear) {
            mPostStorage.saveAllWithClear(posts, channelId, callback);
        } else {
            mPostStorage.saveAll(posts, callback);
        }
    }
}

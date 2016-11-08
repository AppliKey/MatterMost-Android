package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.PendingPost;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class ChatPresenter extends BasePresenter<ChatView> {

    private static final String TAG = ChatPresenter.class.getSimpleName();

    private static final int PAGE_SIZE = 60;

    @Inject
    PostStorage mPostStorage;

    @Inject
    TeamStorage mTeamStorage;

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

    private int mCurrentPage;
    private Channel mChannel;

    public ChatPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChatView view = getViewState();

        updateLastViewedAt(channelId);
        mSubscription.add(mChannelStorage.channel(channelId)
                .subscribe(channel -> mChannel = channel, mErrorHandler::handleError));
        mSubscription.add(mPostStorage.listByChannel(channelId)
                .first()
                .subscribe(view::onDataReady, mErrorHandler::handleError));
    }

    public void channelNameClick() {
        final ChatView view = getViewState();
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(mChannel.getType())) {
            view.openUserProfile(mChannel.getDirectCollocutor());
        } else {
            view.openChannelDetails(mChannel);
        }
    }

    private void updateLastViewedAt(String channelId) {
        // Does not belong to UI
        mTeamStorage.getChosenTeam()
                .first()
                .observeOn(Schedulers.io())
                .flatMap(team -> mApi.updateLastViewedAt(team.getId(), channelId))
                .toCompletable()
                .subscribe(() -> {}, mErrorHandler::handleError);

        mChannelStorage.updateLastViewedAt(channelId);
        mNotificationManager.dismissNotification(channelId);
    }

    public void fetchData(String channelId) {
        getViewState().showProgress(true);
        Timber.d("fetching data");
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team ->
                        mApi.getPostsPage(team.getId(), channelId, mCurrentPage * PAGE_SIZE, PAGE_SIZE)
                                .subscribeOn(Schedulers.io())
                                .doOnError(mErrorHandler::handleError)
                )
                .switchIfEmpty(Observable.empty())
                .map(response -> transform(response, mCurrentPage * PAGE_SIZE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        posts -> {
                            mPostStorage.saveAll(posts);
                            getViewState().showProgress(false);
                            if (!posts.isEmpty()) {
                                mCurrentPage++;
                            }
                        },
                        error -> {
                            getViewState().showProgress(false);
                            mErrorHandler.handleError(error);
                        }));
    }

    public void deleteMessage(String channelId, Post post) {
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.deletePost(team.getId(), channelId, post.getId())
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(posts -> mPostStorage.delete(post))
                .doOnNext(posts -> mChannel.setLastPost(null))
                .subscribe(posts -> mChannelStorage.updateLastPost(mChannel), mErrorHandler::handleError));
    }

    public void editMessage(String channelId, Post post, String newMessage) {
        final Post finalPost = mPostStorage.copyFromDb(post);
        finalPost.setMessage(newMessage);
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.updatePost(team.getId(), channelId, finalPost)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> mPostStorage.update(finalPost), mErrorHandler::handleError));
    }

    public void sendMessage(String channelId, String message) {
        if (TextUtils.isEmpty(message.trim())) {
            return;
        }
        final String currentUserId = mPrefs.getCurrentUserId();
        final long createdAt = System.currentTimeMillis();
        final String pendingId = currentUserId + ":" + createdAt;

        final PendingPost pendingPost = new PendingPost(createdAt, currentUserId, channelId,
                message, "", pendingId);

        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createPost(team.getId(), channelId, pendingPost)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(post -> mChannelStorage.setLastViewedAt(channelId, post.getCreatedAt()))
                .doOnNext(post -> mChannel.setLastPost(post))
                .doOnNext(result -> mChannelStorage.updateLastPost(mChannel))
                .subscribe(result -> getViewState().onMessageSent(result.getCreatedAt()), mErrorHandler::handleError));
    }

    public void sendReplyMessage(String channelId, String message, String mRootId) {
        final String currentUserId = mPrefs.getCurrentUserId();
        final long createdAt = System.currentTimeMillis();
        final String pendingId = currentUserId + ":" + createdAt;

        final PendingPost pendingPost = new PendingPost(createdAt, currentUserId, channelId,
                message, "", pendingId, mRootId, mRootId);

        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createPost(team.getId(), channelId, pendingPost)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(post -> mChannelStorage.setLastViewedAt(channelId, post.getCreatedAt()))
                .doOnNext(post -> mChannel.setLastPost(post))
                .doOnNext(result -> mChannelStorage.updateLastPost(mChannel))
                .subscribe(result -> getViewState().onMessageSent(result.getCreatedAt()), mErrorHandler::handleError));
    }

    private List<Post> transform(PostResponse response, int offset) {
        final List<String> order = response.getOrder();
        for (int i = 0; i < order.size(); i++) {
            final String id = order.get(i);
            response.getPosts().get(id).setPriority(i + offset);
        }
        return new ArrayList<>(response.getPosts().values());
    }
}

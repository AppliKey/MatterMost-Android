package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
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

    private int mCurrentPage;

    public ChatPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChatView view = getViewState();

        updateLastViewedAt(channelId);

        mPostStorage.listByChannel(channelId)
                .first()
                .subscribe(view::onDataReady, view::onFailure);
    }

    private void updateLastViewedAt(String channelId) {
        mSubscription.add(mTeamStorage.getChosenTeam()
                .first()
                .observeOn(Schedulers.io())
                .flatMap(team -> mApi.updateLastViewedAt(team.getId(), channelId))
                .toCompletable()
                .subscribe(ErrorHandler::handleError, () -> {
                }));

        mChannelStorage.updateLastViewedAt(channelId, System.currentTimeMillis());
    }

    public void fetchData(String channelId) {
        getViewState().showProgress(true);
        Timber.d("fetching data");
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team ->
                        mApi.getPostsPage(team.getId(), channelId, mCurrentPage * PAGE_SIZE, PAGE_SIZE)
                                .subscribeOn(Schedulers.io())
                                .doOnError(ErrorHandler::handleError)
                )
                .switchIfEmpty(Observable.empty())
                .doOnNext(v -> Log.d("offset", String.valueOf(mCurrentPage * PAGE_SIZE)))
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
                            ErrorHandler.handleError(error);
                        },
                        () -> getViewState().showProgress(false)));
    }

    public void deleteMessage(String channelId, Post post) {
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.deletePost(team.getId(), channelId, post.getId())
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> mPostStorage.delete(post), ErrorHandler::handleError));
    }

    //FIXME Currently we have problem when sending RealmProxy object to server
    public void editMessage(String channelId, Post post) {
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.updatePost(team.getId(), channelId, post)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> mPostStorage.update(post), ErrorHandler::handleError));
    }

    public void sendMessage(String channelId, String message) {
        final String currentUserId = mPrefs.getCurrentUserId();
        final long createdAt = System.currentTimeMillis();
        final String pendingId = currentUserId + ":" + createdAt;

        final long lastViewedAt = System.currentTimeMillis();
        mChannelStorage.updateLastViewedAt(channelId, lastViewedAt);

        final PendingPost pendingPost = new PendingPost(createdAt, currentUserId, channelId,
                message, "", pendingId);

        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.createPost(team.getId(), channelId, pendingPost)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(result -> mPostStorage.update(result))
                .subscribe(result -> getViewState().onMessageSent(lastViewedAt), ErrorHandler::handleError));
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

package com.applikey.mattermost.mvp.presenters;

import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.MemberInfo;
import com.applikey.mattermost.models.post.LastPostDto;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.web.ChannelExtraResult;
import com.applikey.mattermost.models.web.ChannelWithUsers;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseChatListPresenter extends BasePresenter<ChatListView> implements ChatListPresenter {

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    PostStorage mPostStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ErrorHandler mErrorHandler;

    private String mTeamId;

    private final Set<String> mPreviewLoadedChannels = new HashSet<>();
    private final Set<String> mUsersLoadedChannels = new HashSet<>();

    private Emitter<? super String> mChannelEmitter;

    BaseChatListPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        mTeamId = mPrefs.getCurrentTeamId();

        createObservable();
    }

    @Override
    public void displayData() {
        final Subscription subscription = getInitData()
                .first()
                .subscribe(getViewState()::displayInitialData, mErrorHandler::handleError);

        mSubscription.add(subscription);

        listenPostState();
    }

    @Override
    public void getLastPost(Channel channel) {
        if (mPreviewLoadedChannels.contains(channel.getId())) {
            return;
        }
        mPreviewLoadedChannels.add(channel.getId());
        mChannelEmitter.onNext(channel.getId());
    }

    @Override
    public void getChatUsers(Channel channel) {
        if (mUsersLoadedChannels.contains(channel.getId())) {
            return;
        }
        mUsersLoadedChannels.add(channel.getId());

        final Subscription subscription = mApi.getChannelExtra(mTeamId, channel.getId())
                        .subscribeOn(Schedulers.io())
                .map(extraInfo ->  new ChannelExtraResult(channel, extraInfo))
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .flatMap(channelExtraResult -> mUserStorage.findUsers(
                        Stream.of(channelExtraResult.getExtraInfo().getMembers())
                                .map(MemberInfo::getId)
                                .collect(Collectors.toList())),
                         (channelExtraResult, users) -> new ChannelWithUsers(channelExtraResult.getChannel(), users)) //TODO replace to rx style
                .first()
                .toSingle()
                .subscribe(channelWithUsers -> mChannelStorage.setUsers(channelWithUsers.getChannel().getId(), channelWithUsers.getUsers()), mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private void createObservable() {
        final Observable<String> channelObservable =
                Observable.fromEmitter(emitter -> mChannelEmitter = emitter, Emitter.BackpressureMode.BUFFER);

        final Subscription subscribe = channelObservable
                .observeOn(Schedulers.io())
                .flatMap(channelId -> mApi.getLastPost(mTeamId, channelId).toObservable(), this::transform)
                .filter(lastPostDto -> lastPostDto != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(post -> mChannelStorage.updateLastPosts(post), mErrorHandler::handleError);

        mSubscription.add(subscribe);
    }

    @Nullable
    private LastPostDto transform(String channelId, PostResponse postResponse) {
        final List<Post> posts = Stream.of(postResponse.getPosts())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!posts.isEmpty()) {
            final Post lastPost = posts.get(posts.size() - 1);
            return new LastPostDto(lastPost, channelId);
        }
        return null;
    }

    private void listenPostState() {
        final Subscription subscription = getInitData()
                .doOnNext(channels -> getViewState().displayEmptyState(channels.isEmpty()))
                .map(channels -> Stream.of(channels).filter(Channel::hasUnreadMessages).collect(Collectors.toList()))
                .subscribe(
                        channels -> getViewState().showUnreadIndicator(!channels.isEmpty()),
                        mErrorHandler::handleError);

        mSubscription.add(subscription);
    }
}


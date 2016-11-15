package com.applikey.mattermost.mvp.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.channel.MemberInfo;
import com.applikey.mattermost.models.post.LastPostDto;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.user.User;
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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseChatListPresenter extends BasePresenter<ChatListView>
        implements ChatListPresenter, RealmChangeListener<RealmResults<Channel>> {

    private static final String TAG = BaseChatListPresenter.class.getSimpleName();

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

    private RealmResults<Channel> mChannels;

    private String mTeamId;

    private final Set<String> mPreviewLoadedChannels = new HashSet<>();
    private final Set<String> mUsersLoadedChannels = new HashSet<>();

    private Subscriber<? super String> mChannelSubscriber;

    /* package */ BaseChatListPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        mTeamId = mPrefs.getCurrentTeamId();

        createObservable();
    }

    private void createObservable() {
        final Observable<String> channelObservable = Observable.create(subscriber -> {
            mChannelSubscriber = subscriber;
        });

        final long timeout = 1;
        final long timeShift = 1;

        mSubscription.add(channelObservable
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(channelId -> mApi.getLastPost(mTeamId, channelId), this::transform)
                .buffer(timeout, timeShift, TimeUnit.SECONDS)
                .filter(posts -> !posts.isEmpty())
                .doOnNext(posts -> Log.d(TAG, "createObservable: posts size = " + posts.size()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> mChannelStorage.updateLastPosts(posts), mErrorHandler::handleError));
    }

    @Override
    public void displayData() {
        final Subscription subscription = getInitData()
                .doOnNext(channels -> mChannels = channels)
                .doOnNext(channels -> channels.addChangeListener(this))
                .subscribe(getViewState()::displayInitialData, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    @Override
    public void getLastPost(Channel channel) {
        if (mPreviewLoadedChannels.contains(channel.getId())) {
            return;
        }
        Log.d(TAG, "getLastPost for " + channel.getDisplayName());
        mPreviewLoadedChannels.add(channel.getId());
        mChannelSubscriber.onNext(channel.getId());
    }

    @Override
    public void getChatUsers(Channel channel) {
        if (mUsersLoadedChannels.contains(channel.getId())) {
            return;
        }
        Log.d(TAG, "getUsers for " + channel.getDisplayName());
        mUsersLoadedChannels.add(channel.getId());

        final Subscription subscription = Observable.just(channel)
                .flatMap(ignored -> mApi.getChannelExtra(mTeamId, channel.getId())
                        .subscribeOn(Schedulers.io()), this::transform)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(channelExtraResult -> mUserStorage.findUsers(
                        Stream.of(channelExtraResult.getExtraInfo().getMembers())
                                .map(MemberInfo::getId)
                                .collect(Collectors.toList())), this::transform) //TODO replace to rx style
                .first()
                .subscribe(channelWithUsers -> {
                    mChannelStorage.setUsers(channelWithUsers.getChannel().getId(), channelWithUsers.getUsers());
                }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    @Override
    public void onChange(RealmResults<Channel> channels) {
        final ChatListView view = getViewState();
        if (view == null) {
            throw new NullPointerException("Attached view is null");
        }
        if (channels.size() == 0) {
            view.showUnreadIndicator(false);
            view.showEmpty();
        } else {
            view.showUnreadIndicator(channels.sort(Channel.FIELD_UNREAD_TYPE, Sort.DESCENDING)
                    .first().hasUnreadMessages());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChannels != null) {
            mChannels.removeChangeListener(this);
        }
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

    private ChannelExtraResult transform(Channel channel, ExtraInfo extraInfo) {
        return new ChannelExtraResult(channel, extraInfo);
    }

    private ChannelWithUsers transform(ChannelExtraResult channelExtraResult, List<User> users) {
        return new ChannelWithUsers(channelExtraResult.getChannel(), users);
    }
}


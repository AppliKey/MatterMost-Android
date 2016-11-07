package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
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
    ErrorHandler mErrorHandler;

    private RealmResults<Channel> mChannels;

    private String mTeamId;

    private final Set<String> mLoadedChannels = new HashSet<>();

    /* package */ BaseChatListPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        mTeamId = mPrefs.getCurrentTeamId();
    }

    @Override
    public void displayData() {
        final Subscription subscription = getInitData()
                .first()
                .doOnNext(channels -> mChannels = channels)
                .doOnNext(channels -> channels.addChangeListener(this))
                .subscribe(getViewState()::displayInitialData, mErrorHandler::handleError);
        mSubscription.add(subscription);
    }

    @Override
    public void getLastPost(Channel channel) {
        if (mLoadedChannels.contains(channel.getId())) {
            return;
        }
        Log.d(TAG, "getLastPost for " + channel.getDisplayName());
        mLoadedChannels.add(channel.getId());
        mSubscription.add(mApi.getLastPost(mTeamId, channel.getId())
                .map(this::transform)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(post -> mChannelStorage.setLastPost(channel, post), mErrorHandler::handleError));
    }

    private Post transform(PostResponse postResponse) {
        final List<Post> posts = Stream.of(postResponse.getPosts())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        Post lastPost = null;
        if (!posts.isEmpty()) {
            lastPost = posts.get(posts.size() - 1);
        }
        return lastPost;
    }

    @Override
    public void onChange(RealmResults<Channel> channels) {
        final ChatListView view = getViewState();
        if (view == null) {
            throw new NullPointerException("Attached view is null");
        }
        if (channels.size() == 0) {
            view.showUnreadIndicator(false);
        } else {
            view.showUnreadIndicator(channels.sort(Channel.FIELD_UNREAD_TYPE, Sort.DESCENDING)
                    .first().hasUnreadMessages());
        }
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        if (mChannels != null) {
            mChannels.removeChangeListener(this);
        }
    }
}


package com.applikey.mattermost.mvp.presenters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchTextChanged;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.DirectChannelRequest;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.channel.MemberInfo;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.post.PostSearchRequest;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.ChannelExtraResult;
import com.applikey.mattermost.models.web.ChannelWithUsers;
import com.applikey.mattermost.mvp.views.SearchView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.Db;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> {

    private static final String TAG = SearchPresenter.class.getSimpleName();

    boolean mChannelsIsFetched = false;

    @Inject
    PostStorage mPostStorage;

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    ErrorHandler mErrorHandler;

    @Inject
    Db mDb;

    protected String mSearchString = Constants.EMPTY_STRING;

    private final Set<String> mUsersLoadedChannels = new HashSet<>();

    private String mTeamId;

    public void init() {
        mTeamId = mPrefs.getCurrentTeamId();
    }

    public void requestNotJoinedChannels() {
        mSubscription.add(mApi.getChannelsUserHasNotJoined(mPrefs.getCurrentTeamId())
                                  .subscribeOn(Schedulers.io())
                                  .map(ChannelResponse::getChannels)
                                  .toObservable()
                                  .flatMap(Observable::from)
                                  .doOnNext(channel -> channel.setJoined(false))
                                  .toList()
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .doOnNext(channels -> mChannelStorage.saveAndDeleteRemovedChannelsSync(channels))
                                  .subscribe(channels -> {
                                      mChannelsIsFetched = true;
                                      getData(mSearchString);
                                  }, mErrorHandler::handleError));
    }

    public void handleItemClick(SearchItem item) {
        final SearchView view = getViewState();
        switch (item.getSearchType()) {
            case CHANNEL:
                view.startChatView((Channel) item);
                break;
            case MESSAGE:
                view.startMessageDetailsView( ((Message) item).getPost().getId());
                break;
            case USER:
                final User user = ((User) item);
                mSubscription.add(mChannelStorage.getChannel(user.getId())
                                          .toObservable()
                                          .doOnError(t -> createChannel(user))
                                          .onErrorResumeNext(t -> Observable.empty())
                                          .subscribe(view::startChatView, mErrorHandler::handleError));
                break;
            case MESSAGE_CHANNEL:
                view.startChatView(((Message) item).getChannel());
                break;
        }
    }

    protected Observable<List<SearchItem>> getPostsObservable(String text) {
        return mApi.searchPosts(mPrefs.getCurrentTeamId(), new PostSearchRequest(text))
                .map(PostResponse::getPosts)
                .filter(postMap -> postMap != null)
                .map(Map::values)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(posts -> mPostStorage.saveAllSync(new ArrayList<>(posts)))
                .flatMap(Observable::from)
                .flatMap(item -> mChannelStorage.channelById(item.getChannelId()).first(),
                         Message::new)
                .flatMap(item -> mUserStorage.getDirectProfile(item.getPost().getUserId()).first(),
                         (message, user) -> {
                             message.setUser(user);
                             return (SearchItem) message;
                         })
                .toList();
    }

    private void createChannel(User user) {
        final SearchView view = getViewState();
        view.showLoading(true);

        final Subscription subscription = Observable.just(mPrefs.getCurrentTeamId())
                .observeOn(Schedulers.io())
                .flatMap(teamId -> mApi.createChannel(teamId, new DirectChannelRequest(user.getId())),
                         (team, channel) -> channel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(channel -> channel.setDirectCollocutor(user))
                .doOnNext(channel -> mChannelStorage.save(channel))
                .doOnNext(channel ->
                                  mChannelStorage.updateDirectChannelData(channel,
                                                                          Collections.singletonMap(user.getId(), user),
                                                                          mPrefs.getCurrentUserId()))
                .subscribe(channel -> {
                    view.startChatView(channel);
                    view.showLoading(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    view.showLoading(false);
                });

        mSubscription.add(subscription);
    }

    public void getData(String text) {
        final SearchView view = getViewState();
        view.setEmptyState(true);
        if (!isDataRequestValid(text.trim())) {
            mSubscription.clear();
            view.displayData(null);
            return;
        }
        doRequest(view, text);
    }

    protected void onInputTextChanged(SearchTextChanged event) {
        String text = event.getText();
        if (mSearchString.equals(text)) {
            return;
        }
        mSearchString = text;
        final SearchView view = getViewState();
        view.setSearchText(text);
        view.clearData();
        getData(mSearchString);
    }

    public void getChatUsers(Channel channel, int position) {
        if (mUsersLoadedChannels.contains(channel.getId())) {
            return;
        }
        mUsersLoadedChannels.add(channel.getId());
        final SearchView view = getViewState();

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
                    mChannelStorage.setUsers(channel.getId(), channelWithUsers.getUsers(),
                                             () -> view.notifyItemChanged(position));
                }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private ChannelExtraResult transform(Channel channel, ExtraInfo extraInfo) {
        return new ChannelExtraResult(channel, extraInfo);
    }

    private ChannelWithUsers transform(ChannelExtraResult channelExtraResult, List<User> users) {
        return new ChannelWithUsers(channelExtraResult.getChannel(), users);
    }

    public abstract boolean isDataRequestValid(String text);

    public abstract void doRequest(SearchView view, String text);
}

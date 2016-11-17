package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.post.PostSearchRequest;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.SearchView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.ObjectNotFoundException;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> {

    boolean mChannelsIsFetched = false;

    private List<Channel> mFetchedChannels = new ArrayList<>();

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

    public void requestNotJoinedChannels() {
        mSubscription.add(mTeamStorage.getChosenTeam()
                                  .map(Team::getId)
                                  .observeOn(Schedulers.io())
                                  .flatMap(id -> mApi.getChannelsUserHasNotJoined(id),
                                           (id, channelResponse) -> channelResponse)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(channelResponse -> {
                                      mFetchedChannels = channelResponse.getChannels();
                                      mChannelsIsFetched = true;
                                      getData("");
                                  }, mErrorHandler::handleError));
    }

    List<Channel> addFilterChannels(List<Channel> channels, String text) {
        for (Channel channel : mFetchedChannels) {
            String name = channel.getName();
            if (!TextUtils.isEmpty(name) && name.contains(text)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public void handleItemClick(SearchItem item) {
        SearchView view = getViewState();
        switch (item.getSearchType()) {
            case SearchItem.CHANNEL:
                view.startChatView((Channel) item);
                break;
            case SearchItem.MESSAGE:
                view.startChatView(((Message) item).getChannel());
                break;
            case SearchItem.USER:
                final User user = ((User) item);
                mSubscription.add(mChannelStorage.getChannel(user.getId())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          // TODO CODE SMELLS
                                          .doOnError(throwable -> {
                                              if (throwable instanceof ObjectNotFoundException) {
                                                  createChannel(user);
                                              }
                                          })
                                          .subscribe(view::startChatView,
                                                     mErrorHandler::handleError));
                break;
            case SearchItem.MESSAGE_CHANNEL:
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
                .flatMap(Observable::from)
                .flatMap(item -> mChannelStorage.channelById(item.getChannelId()).first(),
                         Message::new)
                .flatMap(item -> mUserStorage.getDirectProfile(item.getPost().getUserId()).first(),
                         (Func2<Message, User, SearchItem>) (message, user) -> {
                             message.setUser(user);
                             return message;
                         })
                .toList();
    }

    void createChannel(User user) {
    }

    public void getData(String text){
        if (!isDataRequestValid(text)) {
            return;
        }
        final SearchView view = getViewState();
        view.setEmptyState(true);
        doRequest(view, text);
    }

    public abstract boolean isDataRequestValid(String text);

    public abstract void doRequest(SearchView view, String text);
}

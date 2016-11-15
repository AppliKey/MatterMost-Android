package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.SearchView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.ObjectNotFoundException;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

// TODO: Refactor
public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> {

    boolean mChannelsIsFetched = false;

    private List<Channel> mFetchedChannels = new ArrayList<>();

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    Api mApi;

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

    void createChannel(User user) {
    }

    public abstract void getData(String text);
}

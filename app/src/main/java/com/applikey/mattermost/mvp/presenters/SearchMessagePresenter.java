package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchMessageTextChanged;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.post.PostSearchRequest;
import com.applikey.mattermost.mvp.views.SearchMessageView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@InjectViewState
public class SearchMessagePresenter extends SearchPresenter<SearchMessageView> {

    private static final String TAG = SearchMessagePresenter.class.getSimpleName();

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Prefs mPrefs;

    @Inject
    EventBus mEventBus;

    @Inject
    ErrorHandler mErrorHandler;

    public SearchMessagePresenter() {
        App.getUserComponent().inject(this);
        mEventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    public void getData(String text) {
        if (!mChannelsIsFetched) {
            return;
        }
        final SearchMessageView view = getViewState();
        view.setEmptyState(true);
        mSubscription.clear();
        mSubscription.add(
                mApi.searchPosts(mPrefs.getCurrentTeamId(), new PostSearchRequest(text))
                        .map(PostResponse::getPosts)
                        .map(Map::values)
                        .debounce(Constants.INPUT_REQUEST_TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .doOnNext(posts -> Log.d(TAG, "getData init size: " + posts.size()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(Observable::from)
                        .flatMap(item -> mChannelStorage.channelById(item.getChannelId()),
                                 (Func2<Post, Channel, SearchItem>) Message::new)
                        .toList()
                        .subscribe(view::displayData,
                                   mErrorHandler::handleError)
                         );
    }

    @Subscribe
    public void onInputTextChanged(SearchMessageTextChanged event) {
        final SearchMessageView view = getViewState();
        view.clearData();
        getData(event.getText());
    }

    public void handleClick(Message message) {
        final SearchMessageView view = getViewState();
        view.startChatView(message.getChannel());
    }
}

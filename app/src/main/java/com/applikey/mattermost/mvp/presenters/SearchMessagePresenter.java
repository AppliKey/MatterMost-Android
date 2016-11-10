package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchMessageTextChanged;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.post.PostSearchRequest;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.views.SearchMessageView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
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
    public void unSubscribe() {
        super.unSubscribe();
        mEventBus.unregister(this);
    }

    public void getData(String text) {
        if (!mChannelsIsFetched) {
            return;
        }
        final SearchMessageView view = getViewState();
        mSubscription.clear();
        mSubscription.add(
                mTeamStorage.getChosenTeam()
                        .debounce(Constants.INPUT_REQUEST_TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .map(Team::getId)
                        .observeOn(Schedulers.io())
                        .flatMap(teamId -> mApi.searchPosts(teamId, new PostSearchRequest(text)),
                                (Func2<String, PostResponse, List<Post>>)
                                        (s, postResponse) -> new ArrayList<>(postResponse.getPosts().values()))
                        .doOnNext(posts -> Log.d(TAG, "getData init size: " + posts.size()))
                        .flatMap(Observable::from)
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(item -> mChannelStorage.channel(item.getChannelId()).first(),
                                 (post, channel) -> (SearchItem) new Message(post, channel))
                        // TODO: 10.11.16 NEED FIX(TOLIST DOES NOT WORK)
                        .toList()
                        .subscribe(item -> Log.d(TAG, "getData final: " + item), mErrorHandler::handleError));
    }

    @Subscribe
    public void onInputTextChanged(SearchMessageTextChanged event) {
        final SearchMessageView view = getViewState();
        view.clearData();
        getData(event.getText());
    }

    public void handleClick(Post post){
        // TODO: 10.11.16 Implement
    }
}

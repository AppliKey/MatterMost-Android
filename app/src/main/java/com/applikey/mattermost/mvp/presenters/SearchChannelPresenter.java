package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchChannelTextChanged;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.SearchChannelView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class SearchChannelPresenter extends SearchPresenter<SearchChannelView> {

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    Prefs mPrefs;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ErrorHandler mErrorHandler;

    public SearchChannelPresenter() {
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
        final SearchChannelView view = getViewState();
        mSubscription.add(
                mChannelStorage.listUndirected(text)
                        .map(Channel::getList)
                        .observeOn(Schedulers.io())
                        .doOnNext(channels -> addFilterChannels(channels, text))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayData, mErrorHandler::handleError));
    }

    public void handleChannelClick(Channel channel) {
        final SearchChannelView view = getViewState();
        view.startChatView(channel);
    }

    @Subscribe
    public void on(SearchChannelTextChanged event) {
        final SearchChannelView view = getViewState();
        view.clearData();
        getData(event.getText());
    }
}

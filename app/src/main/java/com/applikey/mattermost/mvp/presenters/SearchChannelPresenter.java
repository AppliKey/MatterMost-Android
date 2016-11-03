package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchUserTextChanged;
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

/**
 * @author Anatoliy Chub
 */
@InjectViewState
public class SearchChannelPresenter extends SearchPresenter<SearchChannelView> {

    private static final String TAG = SearchChannelPresenter.class.getSimpleName();

    @Inject
    ChannelStorage mChannelStorage;


    @Inject
    Prefs mPrefs;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;



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
        if(!mChannelsIsFetched){
            return;
        }
        final SearchChannelView view = getViewState();
        mSubscription.add(
                mChannelStorage.listUndirected(text)
                        .map(Channel::getList)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((channels) -> {
                            channels.addAll(0, mFetchedChannels);
                            view.displayData(channels);
                        }, ErrorHandler::handleError));
    }

    public void handleChannelClick(Channel channel) {
        final SearchChannelView view = getViewState();
        view.startChatActivity(channel);
    }

    @Subscribe
    public void onTextChanged(SearchUserTextChanged event) {
        final SearchChannelView view = getViewState();
        view.clearData();
        getData(event.getText());
    }



}

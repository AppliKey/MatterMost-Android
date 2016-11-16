package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchChannelTextChanged;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.SearchChannelView;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class SearchChannelPresenter extends SearchPresenter<SearchChannelView> {

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
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    public void getData(String text) {
        if (!mChannelsIsFetched  || TextUtils.isEmpty(text)) {
            return;
        }
        final SearchChannelView view = getViewState();
        view.setEmptyState(true);
        mSubscription.clear();
        mSubscription.add(
                mChannelStorage.listUndirected(text)
                        .map(Channel::getList)
                        .observeOn(Schedulers.io())
                        .doOnNext(channels -> addFilterChannels(channels, text))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(channels -> view.displayData(new ArrayList<>(channels)), mErrorHandler::handleError));
    }

    @Subscribe
    public void onInputTextChanged(SearchChannelTextChanged event) {
        final SearchChannelView view = getViewState();
        view.clearData();
        getData(event.getText());
    }
}

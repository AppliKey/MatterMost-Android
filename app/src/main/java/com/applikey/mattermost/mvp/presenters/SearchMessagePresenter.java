package com.applikey.mattermost.mvp.presenters;

import android.text.TextUtils;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchMessageTextChanged;
import com.applikey.mattermost.mvp.views.SearchMessageView;
import com.applikey.mattermost.mvp.views.SearchView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

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

    @Override
    public boolean isDataRequestValid(String text) {
        return mChannelsIsFetched && !TextUtils.isEmpty(text);
    }

    @Override
    public void doRequest(SearchView view, String text) {
        mSubscription.clear();
        mSubscription.add(getPostsObservable(text)
                                  .debounce(Constants.INPUT_REQUEST_TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                                  .subscribe(view::displayData,
                                             mErrorHandler::handleError)
                         );
    }

    @Subscribe
    public void onInputTextChanged(SearchMessageTextChanged event) {
        mSearchString = event.getText();
        final SearchMessageView view = getViewState();
        view.clearData();
        getData(mSearchString);
    }

}

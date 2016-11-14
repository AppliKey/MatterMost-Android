package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchAllTextChanged;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.SearchAllView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class SearchAllPresenter extends SearchPresenter<SearchAllView> {

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ErrorHandler mErrorHandler;

    public SearchAllPresenter() {
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
        final SearchAllView view = getViewState();
        mSubscription.add(
                Observable.zip(
                        Channel.getList(mChannelStorage.listUndirected(text))
                                .doOnNext(channels -> addFilterChannels(channels, text)),
                        mUserStorage.searchUsers(text), (items, users) -> {

                            final List<SearchItem> searchItemList = new ArrayList<>();

                            for (Channel item : items) {
                                searchItemList.add(item);
                            }
                            for (User user : users) {
                                searchItemList.add(user);
                            }

                            return searchItemList;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayData, mErrorHandler::handleError));
    }

    @Subscribe
    public void on(SearchAllTextChanged event) {
        final SearchAllView view = getViewState();
        view.clearData();
        getData(event.getText());
    }
}

package com.applikey.mattermost.mvp.presenters;

import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.events.SearchUserTextChanged;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
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

/**
 * @author Anatoliy Chub
 */
@InjectViewState
public class SearchAllPresenter extends SearchPresenter<SearchAllView> {

    private static final String TAG = SearchAllPresenter.class.getSimpleName();

    @Inject
    ChannelStorage mChannelStorage;

    @Inject
    UserStorage mUserStorage;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    public SearchAllPresenter() {
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
        final SearchAllView view = getViewState();
        mSubscription.add(
                Observable.zip(
                        Channel.getList(mChannelStorage.listUndirected(text))
                                .map(channels -> {
                                    channels.addAll(0, mFetchedChannels);
                                    return channels;
                                }),
                        mUserStorage.searchUsers(text), (items, users) -> {

                            List<SearchItem> searchItemList = new ArrayList<>();

                            for (int i = 0; i < items.size(); i++) {
                                searchItemList.add(items.get(i));
                            }
                            for (int i = 0; i < users.size(); i++) {
                                searchItemList.add(users.get(i));
                            }

                            return searchItemList;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {
                            Log.d(TAG, "getData: " + items);
                            view.displayData(items);
                        }, ErrorHandler::handleError));
    }


    @Subscribe
    public void onTextChanged(SearchUserTextChanged event) {
        final SearchAllView view = getViewState();
        view.clearData();
        getData(event.getText());
    }

}

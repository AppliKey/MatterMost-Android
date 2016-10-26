package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;

import javax.inject.Inject;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class BaseChatListPresenter extends BasePresenter<ChatListView>
        implements ChatListPresenter, RealmChangeListener<RealmResults<Channel>> {

    @Inject
    Prefs mPrefs;

    @Inject
    Api mApi;

    @Inject
    ChannelStorage mChannelStorage;

    private RealmResults<Channel> mChannels;

    /* package */ BaseChatListPresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        final ChatListView view = getViewState();
        if (view == null) {
            throw new NullPointerException("Attached view is null");
        }
        mSubscription.add(getInitData()
                .doOnNext(channels -> mChannels = channels)
                .doOnNext(channels -> channels.addChangeListener(this))
                .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }

    @Override
    public void onChange(RealmResults<Channel> channels) {
        final ChatListView view = getViewState();
        if (view == null) {
            throw new NullPointerException("Attached view is null");
        }
        if (channels.size() == 0) {
            view.showUnreadIndicator(false);
        } else {
            view.showUnreadIndicator(channels.sort(Channel.FIELD_UNREAD_TYPE, Sort.DESCENDING)
                    .first().hasUnreadMessages());
        }
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        mChannels.removeChangeListener(this);
    }
}

